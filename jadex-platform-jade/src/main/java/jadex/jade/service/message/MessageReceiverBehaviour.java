package jadex.jade.service.message;

import jade.content.Concept;
import jade.content.ContentElementList;
import jade.content.ContentManager;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Done;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Deregister;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.Modify;
import jade.domain.FIPAAgentManagement.Register;
import jade.domain.FIPAAgentManagement.Search;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.KillAgent;
import jade.lang.acl.ACLMessage;
import jade.util.leap.List;
import jadex.base.fipa.CMSCreateComponent;
import jadex.base.fipa.CMSDestroyComponent;
import jadex.base.fipa.CMSResumeComponent;
import jadex.base.fipa.CMSSearchComponents;
import jadex.base.fipa.CMSSuspendComponent;
import jadex.base.fipa.DFDeregister;
import jadex.base.fipa.DFModify;
import jadex.base.fipa.DFRegister;
import jadex.base.fipa.DFSearch;
import jadex.base.fipa.IComponentAction;
import jadex.base.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.df.IDFComponentDescription;
import jadex.bridge.service.types.message.IContentCodec;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.DefaultResultListener;
import jadex.jade.JadeComponentAdapter;
import jadex.jade.JadeMessageAdapter;
import jadex.jade.SJade;


/**
 * The message receiver behaviour listens for incoming messages, and creates an
 * appropriate event for every received message. The message receiver behaviour
 * is the basic running behaviour for the bdi agent. It is used to receive all
 * messages and invokes the event dispatcher when a new message arrives.
 */
public class MessageReceiverBehaviour extends CyclicBehaviour
{
	// -------- attributes --------
	
	/** The jadex agent. */
	protected JadeComponentAdapter		agent;

	// -------- constructors --------

	/**
	 * Create the message receiver behaviour.
	 * @param agent The bdi agent.
	 */
	public MessageReceiverBehaviour(JadeComponentAdapter agent)
	{
		this.agent = agent;
	}

	// -------- methods --------

	/**
	 *  The behavior implementation. When a message is received dispatch it.
	 *  When no message is received wait for one. ;-)
	 */
	public void action()
	{
		ACLMessage msg = myAgent.receive();

		if(msg == null)
		{
			// No message -> wait.
			block();
		}
		else
		{
			// Otherwise dispatch message to agent.
			final JadeMessageAdapter ma = new JadeMessageAdapter(msg);
			
			
			// Conversion via platform specific codecs
			IContentCodec[] compcodecs = jadex.base.service.message.MessageService.getContentCodecs(agent.getModel());
			String[] params = ma.getMessageType().getParameterNames();
			for(int i=0; i<params.length; i++)
			{
				IContentCodec codec = ma.getMessageType().findContentCodec(compcodecs, ma, params[i]);
				if(codec==null)
					codec = ma.getMessageType().findContentCodec(jadex.base.service.message.MessageService.CODECS, ma, params[i]);
				if(codec!=null)
				{
					try
					{
						String	val	= (String)ma.getValue(params[i]);
						if(val!=null)
						{
							ClassLoader	cl	= agent.getModel().getClassLoader();
							ma.setDecodedValue(params[i], codec.decode(val.getBytes(), cl));
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			// todo: sets?

			// Hack!!! Convert FIPA AMS/DF messages to Jadex/Nuggets
			if(ma.getMessageType().equals(SFipa.FIPA_MESSAGE_TYPE))
			{
				if(FIPAManagementOntology.NAME.equals(ma.getValue(SFipa.ONTOLOGY)) || JADEManagementOntology.NAME.equals(ma.getValue(SFipa.ONTOLOGY)))
				{
					ContentManager	cm	= new ContentManager();
					cm.registerLanguage(new SLCodec(0));
					cm.registerOntology(FIPAManagementOntology.getInstance());
					cm.registerOntology(JADEManagementOntology.getInstance());
					try
					{
						Object	content	= cm.extractContent(msg);
						Object	jadexcontent	= null;
						if(content instanceof Done)
						{
							Action	action	= (Action)((Done)content).getAction();
							Concept	request	= action.getAction();
							IComponentAction	jadexaction	= null;
							if(request instanceof Register)
							{
								IDFComponentDescription	dfadesc	= SJade.convertAgentDescriptiontoFipa((DFAgentDescription)((Register)request).getDescription());
								jadexaction	= new DFRegister(dfadesc, dfadesc);
							}
							else if(request instanceof Deregister)
							{
								IDFComponentDescription	dfadesc	= SJade.convertAgentDescriptiontoFipa((DFAgentDescription)((Deregister)request).getDescription());
								jadexaction	= new DFDeregister(dfadesc);
							}
							else if(request instanceof Modify)
							{
								if(msg.getSender().getLocalName().toLowerCase().indexOf("cms")!=-1)
								{
									IComponentDescription	amsadesc	= SJade.convertAMSAgentDescriptiontoFipa((AMSAgentDescription)((Modify)request).getDescription());
									if(AMSAgentDescription.SUSPENDED.equals(amsadesc.getState()))
										jadexaction	= new CMSSuspendComponent(amsadesc.getName());
									else
										jadexaction	= new CMSResumeComponent(amsadesc.getName());
									// todo: AMSStartAgent ???
								}
								else
								{
									IDFComponentDescription	dfadesc	= SJade.convertAgentDescriptiontoFipa((DFAgentDescription) ((Modify)request).getDescription());
									jadexaction	= new DFModify(dfadesc, dfadesc);
								}
							}
							else if(request instanceof CreateAgent)
							{
								// Hack!!! Bug in JADE not returning created agent's AID.
								// Should do ams_search do get correct AID?
								AID tmp = (AID)msg.getSender();
								int idx = tmp.getName().indexOf("@");
								tmp.setName(((CreateAgent)request).getAgentName() + tmp.getName().substring(idx));
								jadexaction	= new CMSCreateComponent(SJade.convertAIDtoFipa(tmp));
							}
							else if(request instanceof KillAgent)
							{
								jadexaction	= new CMSDestroyComponent(SJade.convertAIDtoFipa(((KillAgent)request).getAgent()));
							}
							else
							{
								throw new RuntimeException("Action not supported: "+request);
							}
							jadexcontent	= new jadex.base.fipa.Done(jadexaction);
						}
						else if(content instanceof Result)
						{
							Action	action	= (Action)((Result)content).getAction();
							Concept	request	= action.getAction();
							IComponentAction	jadexaction	= null;
							if(request instanceof Search)
							{
								if(msg.getSender().getLocalName().toLowerCase().indexOf("cms")!=-1)
								{
									IComponentDescription	amsadesc	= SJade.convertAMSAgentDescriptiontoFipa((AMSAgentDescription)((Search)request).getDescription());
									List	items	= ((Result)content).getItems();
									IComponentDescription[]	results	= new IComponentDescription[items.size()];
									for(int i=0; i<results.length; i++)
										results[i]	= SJade.convertAMSAgentDescriptiontoFipa((AMSAgentDescription) items.get(i));
									jadexaction	= new CMSSearchComponents(amsadesc, results);
								}
								else
								{
									IDFComponentDescription	dfadesc	= SJade.convertAgentDescriptiontoFipa((DFAgentDescription) ((Search)request).getDescription());
									List	items	= ((Result)content).getItems();
									IDFComponentDescription[]	results	= new IDFComponentDescription[items.size()];
									for(int i=0; i<results.length; i++)
										results[i]	= SJade.convertAgentDescriptiontoFipa((DFAgentDescription) items.get(i));
									jadexaction	= new DFSearch(dfadesc, results);
								}
							}
							else
							{
								throw new RuntimeException("Action not supported: "+request);
							}
							jadexcontent	= new jadex.base.fipa.Done(jadexaction);
						}
						else if(content instanceof ContentElementList)
						{
							// CEL is used to provide information about failures in the form {action, failure-reason}
							// Todo: Failure reasons currently not used in Jadex
//								Action	action	= (Action)((ContentElementList)content).get(0);
//								Predicate	reason	= (Predicate)((ContentElementList)content).get(1);
//								jadexcontent	= ...
						}
						else
						{
							throw new RuntimeException("Content not supported: "+content);
						}
						
//							System.out.println("Converted: "+jadexcontent+", "+msg.getContent());
						ma.setDecodedValue(SFipa.CONTENT, jadexcontent);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			
			agent.getComponentInstance().messageArrived(ma);
			
			SServiceProvider.getService(agent.getServiceContainer(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					MessageService	ms	= (MessageService)result;
					ms.messageReceived(ma);
				}
			});
		}
	}
}
