package jadex.adapter.jade;

import jade.content.Concept;
import jade.content.ContentManager;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.ContainerID;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.Deregister;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.domain.FIPAAgentManagement.Modify;
import jade.domain.FIPAAgentManagement.Register;
import jade.domain.FIPAAgentManagement.Search;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.domain.JADEAgentManagement.KillAgent;
import jade.lang.acl.ACLMessage;
import jadex.adapter.base.fipa.AMSCreateAgent;
import jadex.adapter.base.fipa.AMSDestroyAgent;
import jadex.adapter.base.fipa.AMSResumeAgent;
import jadex.adapter.base.fipa.AMSSearchAgents;
import jadex.adapter.base.fipa.AMSStartAgent;
import jadex.adapter.base.fipa.AMSSuspendAgent;
import jadex.adapter.base.fipa.DFDeregister;
import jadex.adapter.base.fipa.DFModify;
import jadex.adapter.base.fipa.DFRegister;
import jadex.adapter.base.fipa.DFSearch;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IDFComponentDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bridge.ContentException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IContentCodec;
import jadex.bridge.IMessageService;
import jadex.bridge.ISearchConstraints;
import jadex.bridge.MessageType;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.clock.IClockService;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;


/**
 *  The Message service serves several message-oriented purposes: a) sending and
 *  delivering messages by using transports 
 */
public class MessageService implements IMessageService, IService
{
	//-------- constants --------
	
	/** The default codecs. */
	protected static IContentCodec[]	DEFCODECS	= new IContentCodec[]
	{
		new jadex.adapter.base.JavaXMLContentCodec(),
		new jadex.adapter.base.NuggetsXMLContentCodec()
	};
	
	//-------- attributes --------

	/** The ams. */
	protected Platform platform;

	/** The transports. */
//	protected List transports;

	/** All addresses of this platform. */
	private String[] addresses;

	/** The send message action executed by platform executor. */
//	protected SendMessage sendmsg;
	
	/** The deliver message action executed by platform executor. */
//	protected DeliverMessage delivermsg;
	
	/** The logger. */
	protected Logger logger;

	//-------- constructors --------

	/**
	 *  Constructor for Outbox.
	 *  @param platform
	 */
	public MessageService(Platform platform)
	{
		this.platform = platform;
		this.logger = Logger.getLogger("JADE_Platform.mts");
	}
	
	//-------- interface methods --------

	/**
	 *  Send a message.
	 *  @param message The native message.
	 */
	public void sendMessage(Map message, MessageType type, IComponentIdentifier sender, ClassLoader cl)
	{
		if(sender==null)
			throw new RuntimeException("Sender must not be null: "+message);
		
		// Automatically add optional meta information.
		String senid = type.getSenderIdentifier();
		Object sen = message.get(senid);
		if(sen==null)
			message.put(senid, sender);
		
		String idid = type.getIdIdentifier();
		Object id = message.get(idid);
		if(id==null)
			message.put(idid, SUtil.createUniqueId(sender.getLocalName()));

		String sd = type.getTimestampIdentifier();
		Object senddate = message.get(sd);
		if(senddate==null)
		{
			IClockService	clock	= (IClockService) platform.getService(IClockService.class);
			if(clock!=null)
				message.put(sd, ""+clock.getTime());
		}
		
		IComponentIdentifier[] receivers = null;
		Object tmp = message.get(type.getReceiverIdentifier());
		if(tmp instanceof Collection)
			receivers = (IComponentIdentifier[])((Collection)tmp).toArray(new IComponentIdentifier[0]);
		else
			receivers = (IComponentIdentifier[])tmp;
		
		if(receivers==null || receivers==new IComponentIdentifier[0])
		{
			throw new RuntimeException("Receivers must not be empty: "+message);
		}

		// Hack!!! Convert Jadex/Nuggets AMS/DF messages to FIPA.
		if(type.equals(SFipa.FIPA_MESSAGE_TYPE) && receivers.length==1)
		{
			if(SFipa.AGENT_MANAGEMENT_ONTOLOGY_NAME.equals(message.get(SFipa.ONTOLOGY)))
			{
				Object	content	= message.get(SFipa.CONTENT);
				Concept	request	= null;
				String	ontology	= FIPAManagementOntology.NAME;	// Default for most AMS/DF requests
				
				if(content instanceof DFRegister)
				{
					IDFComponentDescription	dfadesc	= ((DFRegister)content).getComponentDescription();
					Register	register	= new Register();
					register.setDescription(SJade.convertAgentDescriptiontoJade(dfadesc));
					request	= register;
				}
				else if(content instanceof DFModify)
				{
					IDFComponentDescription	dfadesc	= ((DFModify)content).getComponentDescription();
					Modify	modify	= new Modify();
					modify.setDescription(SJade.convertAgentDescriptiontoJade(dfadesc));
					request	= modify;
				}
				else if(content instanceof DFSearch)
				{
					IDFComponentDescription	dfadesc	= ((DFSearch)content).getComponentDescription();
					Search	search	= new Search();
					search.setDescription(SJade.convertAgentDescriptiontoJade(dfadesc));
					ISearchConstraints	cons	= ((DFSearch)content).getSearchConstraints();
					if(cons!=null)
					{
						search.setConstraints(SJade.convertSearchConstraintstoJade(cons));
					}
					else
					{
						SearchConstraints	scon	= new SearchConstraints();
						scon.setMaxResults(new Long(-1));
						search.setConstraints(scon);
					}
					request	= search;
				}
				else if(content instanceof DFDeregister)
				{
					IDFComponentDescription	dfadesc	= ((DFDeregister)content).getComponentDescription();
					Deregister	deregister	= new Deregister();
					deregister.setDescription(SJade.convertAgentDescriptiontoJade(dfadesc));
					request	= deregister;
				}
				else if(content instanceof AMSCreateAgent)
				{
					AMSCreateAgent	aca	= (AMSCreateAgent)content;
					if(aca.getName()==null)
					{
						AMS	ams	= (AMS)platform.getService(IAMS.class);
						aca.setName(ams.generateAgentName(ams.getShortName(aca.getType())));
					}
					CreateAgent	create	= new CreateAgent();
					create.setAgentName(aca.getName());
					create.addArguments(aca.getType());
					create.addArguments(aca.getConfiguration()!=null ? aca.getConfiguration() : "");
					if(aca.getArguments()!=null)
						create.addArguments(aca.getArguments());
					create.setClassName(JadeAgentAdapter.class.getName());
					if(!aca.isStart())
						throw new RuntimeException("Delayed agent start not yet supported.");

					create.setContainer(new ContainerID("Main-Container", null));
					
					request	= create;
					ontology	= JADEManagementOntology.NAME;
				}
				else if(content instanceof AMSStartAgent)
				{
					IComponentIdentifier	amsaid	= ((AMSStartAgent)content).getAgentIdentifier();
					Modify	start	= new Modify();
					AMSAgentDescription	adesc	= new AMSAgentDescription();
					adesc.setName(SJade.convertAIDtoJade(amsaid));
					adesc.setState(AMSAgentDescription.ACTIVE);
					start.setDescription(adesc);
					request	= start;
				}
				else if(content instanceof AMSDestroyAgent)
				{
					IComponentIdentifier	amsaid	= ((AMSDestroyAgent)content).getAgentIdentifier();
					KillAgent	destroy	= new KillAgent();
					destroy.setAgent(SJade.convertAIDtoJade(amsaid));
					request	= destroy;
					ontology	= JADEManagementOntology.NAME;
				}
				else if(content instanceof AMSSuspendAgent)
				{
					IComponentIdentifier	amsaid	= ((AMSSuspendAgent)content).getAgentIdentifier();
					Modify	suspend	= new Modify();
					AMSAgentDescription	adesc	= new AMSAgentDescription();
					adesc.setName(SJade.convertAIDtoJade(amsaid));
					adesc.setState(AMSAgentDescription.SUSPENDED);
					suspend.setDescription(adesc);
					request	= suspend;
				}
				else if(content instanceof AMSResumeAgent)
				{
					IComponentIdentifier	amsaid	= ((AMSResumeAgent)content).getAgentIdentifier();
					Modify	resume	= new Modify();
					AMSAgentDescription	adesc	= new AMSAgentDescription();
					adesc.setName(SJade.convertAIDtoJade(amsaid));
					adesc.setState(AMSAgentDescription.ACTIVE);
					resume.setDescription(adesc);
					request	= resume;
				}
				else if(content instanceof AMSSearchAgents)
				{
					Search	search	= new Search();
					AMSAgentDescription	amsadesc	= SJade.convertAMSAgentDescriptiontoJade(((AMSSearchAgents)content).getAgentDescription());
					// Hack !!! Strip addresses/resolvers from aid before search (JADE doesn't store these in AMS and therefore finds no matches, grrr).
					if(amsadesc.getName()!=null)
						amsadesc.setName(new AID(amsadesc.getName().getName(), AID.ISGUID));
					search.setDescription(amsadesc);
					ISearchConstraints	cons	= ((AMSSearchAgents)content).getSearchConstraints();
					if(cons!=null)
					{
						search.setConstraints(SJade.convertSearchConstraintstoJade(cons));
					}
					else
					{
						SearchConstraints	scon	= new SearchConstraints();
						scon.setMaxResults(new Long(-1));
						search.setConstraints(scon);
					}
					request	= search;
				}
				else
				{
					throw new RuntimeException("Action not supported: "+content);
				}

				Action	action	= new Action(SJade.convertAIDtoJade(receivers[0]), request);
				ContentManager	cm	= new ContentManager();
				cm.registerLanguage(new SLCodec(0));
				cm.registerOntology(FIPAManagementOntology.getInstance());
				cm.registerOntology(JADEManagementOntology.getInstance());
				ACLMessage	dummy	= new ACLMessage(0);
				dummy.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
				dummy.setOntology(ontology);
				try
				{
					cm.fillContent(dummy, action);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				message.put(SFipa.CONTENT, dummy.getContent());
				message.put(SFipa.LANGUAGE, FIPANames.ContentLanguage.FIPA_SL0);
				message.put(SFipa.ONTOLOGY, ontology);
			}
		}

		// Conversion via platform specific codecs
		for(Iterator it=message.keySet().iterator(); it.hasNext(); )
		{
			String	name	= (String)it.next();
			Object	value	= message.get(name);
			IContentCodec	codec	= type.findContentCodec(DEFCODECS, message, name);
			if(codec!=null)
			{
				message.put(name, codec.encode(value, cl));
			}
			else if(value!=null && !(value instanceof String) 
				&& !(name.equals(type.getSenderIdentifier()) || name.equals(type.getReceiverIdentifier())))
			{	
				throw new ContentException("No content codec found for: "+name+", "+message);
			}
		}

		// Prepare message for Jade.
		final ACLMessage msg = SJade.convertMessagetoJade(message, type);
		
		// Send message over Jade.
		IAMS ams = (IAMS)platform.getService(IAMS.class);
		ams.getAgentAdapter(sender, new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				JadeAgentAdapter adapter = (JadeAgentAdapter)result;
				adapter.send(msg);
//				System.out.println("message sent: "+msg);
			}
			public void exceptionOccurred(Exception exception)
			{
			}
		});
		
//		sendmsg.addMessage(message, type.getName(), receivers);
	}

	/**
	 *  Deliver a message to the intended agents. Called from transports.
	 *  @param message The native message. 
	 *  (Synchronized because can be called from concurrently executing transports)
	 */
	public synchronized void deliverMessage(Map message, String msgtype, IComponentIdentifier[] receivers)
	{	
		// Not necessary in JADE.
		throw new UnsupportedOperationException();
	}

	/**
	 *  Get the adresses of an agent.
	 *  @return The addresses of this agent.
	 */
	public String[] getAddresses()
	{
		// Hack! Should be looked up dynamically.
		return platform.getPlatformAgent().getAddressesArray();
	}

	//-------- IPlatformService interface --------
	
	/**
	 *  Start the service.
	 */
	public void startService()
	{
	}
	
	/**
	 *  Called when the platform shuts down. Do necessary cleanup here (if any).
	 */
	public void shutdownService(IResultListener listener)
	{
		if(listener!=null)
			listener.resultAvailable(this, null);
	}
}


