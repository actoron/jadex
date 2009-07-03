package jadex.adapter.jade;

import java.util.HashMap;
import java.util.Map;

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
import jadex.adapter.base.fipa.AMSCreateAgent;
import jadex.adapter.base.fipa.AMSDestroyAgent;
import jadex.adapter.base.fipa.AMSResumeAgent;
import jadex.adapter.base.fipa.AMSSearchAgents;
import jadex.adapter.base.fipa.AMSSuspendAgent;
import jadex.adapter.base.fipa.DFDeregister;
import jadex.adapter.base.fipa.DFModify;
import jadex.adapter.base.fipa.DFRegister;
import jadex.adapter.base.fipa.DFSearch;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.IAgentAction;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bridge.IContentCodec;
import jadex.bridge.IKernelAgent;
import jadex.bridge.ILibraryService;


/**
 * The message receiver behaviour listens for incoming messages, and creates an
 * appropriate event for every received message. The message receiver behaviour
 * is the basic running behaviour for the bdi agent. It is used to receive all
 * messages and invokes the event dispatcher when a new message arrives.
 */
public class MessageReceiverBehaviour extends CyclicBehaviour
{
	// -------- constants --------

	/** The jadefilter property identifier. */
//	public static final String	PROPERTY_JADEFILTER		= "jadefilter";

	/** The message preprocessors property identifier. */
//	public static final String	PROPERTY_TOOL_ADAPTERS	= "tooladapter";

	/** The default codecs. */
	protected static IContentCodec[]	DEFCODECS	= new IContentCodec[]
	{
		new jadex.adapter.base.JavaXMLContentCodec(),
		new jadex.adapter.base.NuggetsXMLContentCodec()
	};
	
	// -------- attributes --------

	/** The platform. */
	protected Platform platform;
	
	/** The jadex agent. */
	protected IKernelAgent		agent;

	/** The ams. */
	protected IAMS ams;
	
	/** The positive Jadex filter. Those messages that are forwarded to the Jadex system. */
//	protected MessageTemplate	antiposfilter;

	/** The tool adapters for managing communication with tool agents (tooltype -> adapter). */
//	protected List				tooladapters;

	/** The tool message template. */
//	protected MessageTemplate	toolmsg;

	// -------- constructors --------

	/**
	 * Create the message receiver behaviour.
	 * @param agent The bdi agent.
	 */
	public MessageReceiverBehaviour(Platform platform, IKernelAgent agent, IAMS ams)
	{
		this.platform = platform;
		this.agent = agent;
		this.ams = ams;

		// Get the JADE filter when specified.
//		MessageTemplate posfilter = (MessageTemplate)agent.getProperty(PROPERTY_JADEFILTER);
//		if(posfilter != null) this.antiposfilter = MessageTemplate.not(posfilter);
		// System.out.println("jadefilter: "+posfilter);

		// Get all declared tool adapters, that have to be added.
//		this.toolmsg = MessageTemplate.and(new MessageTemplate(new MatchExpression()
//		{
//			public boolean match(ACLMessage arg0)
//			{
//				String o = arg0.getOntology();
//				return o != null && o.toLowerCase().startsWith("jadex.tools");
//			}
//		}), MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
//		
//		this.tooladapters = SCollection.createArrayList();
//		String[] keys = agent.getPropertyNames(PROPERTY_TOOL_ADAPTERS);
//		for(int i = 0; i < keys.length; i++)
//		{
//			IToolAdapter adapter = (IToolAdapter)agent.getProperty(keys[i]);
//			tooladapters.add(adapter);
//		}
	}

	// -------- methods --------

	/**
	 * The behaviour implementation. When a message is received notify
	 * dispatcher. When no message is received wait for one;-)
	 */
	public void action()
	{
		// agent.invokeSynchronized(code);
		// Get the next message.
		// todo: implement a negative filter (all wait-filters from jadex) ?!
//		ACLMessage msg = myAgent.receive(antiposfilter);
		ACLMessage msg = myAgent.receive();

		if(msg == null)
		{
			// No message -> wait.
			block();
		}
		else
		{
			// Check for tool message.
//			if(toolmsg.match(msg))
//			{
//				handleToolMessage(msg);
				// todo:
//			}

			// Otherwise dispatch message to agent.
//			else
			{
				
//				IMessageAdapter ma = new DefaultMessageAdapter();
//				agent.messageArrived(ma);
				
				Map	decoded	= new HashMap();	// Decoded messages cached by class loader to avoid decoding the same message more than once, when the same class loader is used.
				JadeMessageAdapter ma = new JadeMessageAdapter(msg, ams);
				
				// Conversion via platform specific codecs
				String[] params = ma.getMessageType().getParameterNames();
				for(int i=0; i<params.length; i++)
				{
					IContentCodec codec = ma.getMessageType().findContentCodec(DEFCODECS, ma, params[i]);
					if(codec!=null)
					{
						try
						{
							String	val	= (String)ma.getValue(params[i]);
							if(val!=null)
							{
								ClassLoader	cl	= agent.getClassLoader();
								ma.setDecodedValue(params[i], codec.decode(val, cl));
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
								IAgentAction	jadexaction	= null;
								if(request instanceof Register)
								{
									IDFAgentDescription	dfadesc	= SJade.convertAgentDescriptiontoFipa((DFAgentDescription) ((Register)request).getDescription(), ams);
									jadexaction	= new DFRegister(dfadesc, dfadesc);
								}
								else if(request instanceof Deregister)
								{
									IDFAgentDescription	dfadesc	= SJade.convertAgentDescriptiontoFipa((DFAgentDescription) ((Deregister)request).getDescription(), ams);
									jadexaction	= new DFDeregister(dfadesc);
								}
								else if(request instanceof Modify)
								{
									if(msg.getSender().getLocalName().toLowerCase().indexOf("ams")!=-1)
									{
										IAMSAgentDescription	amsadesc	= SJade.convertAMSAgentDescriptiontoFipa((AMSAgentDescription)((Modify)request).getDescription(), ams);
										if(AMSAgentDescription.SUSPENDED.equals(amsadesc.getState()))
											jadexaction	= new AMSSuspendAgent(amsadesc.getName());
										else
											jadexaction	= new AMSResumeAgent(amsadesc.getName());
										// todo: AMSStartAgent ???
									}
									else
									{
										IDFAgentDescription	dfadesc	= SJade.convertAgentDescriptiontoFipa((DFAgentDescription) ((Modify)request).getDescription(), ams);
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
									jadexaction	= new AMSCreateAgent(SJade.convertAIDtoFipa(tmp, ams));
								}
								else if(request instanceof KillAgent)
								{
									jadexaction	= new AMSDestroyAgent(SJade.convertAIDtoFipa(((KillAgent)request).getAgent(), ams));
								}
								else
								{
									throw new RuntimeException("Action not supported: "+request);
								}
								jadexcontent	= new jadex.adapter.base.fipa.Done(jadexaction);
							}
							else if(content instanceof Result)
							{
								Action	action	= (Action)((Result)content).getAction();
								Concept	request	= action.getAction();
								IAgentAction	jadexaction	= null;
								if(request instanceof Search)
								{
									if(msg.getSender().getLocalName().toLowerCase().indexOf("ams")!=-1)
									{
										IAMSAgentDescription	amsadesc	= SJade.convertAMSAgentDescriptiontoFipa((AMSAgentDescription)((Search)request).getDescription(), ams);
										List	items	= ((Result)content).getItems();
										IAMSAgentDescription[]	results	= new IAMSAgentDescription[items.size()];
										for(int i=0; i<results.length; i++)
											results[i]	= SJade.convertAMSAgentDescriptiontoFipa((AMSAgentDescription) items.get(i), ams);
										jadexaction	= new AMSSearchAgents(amsadesc, results);
									}
									else
									{
										IDFAgentDescription	dfadesc	= SJade.convertAgentDescriptiontoFipa((DFAgentDescription) ((Search)request).getDescription(), ams);
										List	items	= ((Result)content).getItems();
										IDFAgentDescription[]	results	= new IDFAgentDescription[items.size()];
										for(int i=0; i<results.length; i++)
											results[i]	= SJade.convertAgentDescriptiontoFipa((DFAgentDescription) items.get(i), ams);
										jadexaction	= new DFSearch(dfadesc, results);
									}
								}
								else
								{
									throw new RuntimeException("Action not supported: "+request);
								}
								jadexcontent	= new jadex.adapter.base.fipa.Done(jadexaction);
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
				
				agent.messageArrived(ma);
			}
		}
	}

	// -------- tool message handling --------

	/**
	 * Handle a tool message.
	 * /
	public void handleToolMessage(final ACLMessage msg)
	{
		// Hack!!! JADE Bug sending AMS failure with Tools ontology
		if(msg.getPerformative() == ACLMessage.FAILURE) return;

		AgentIdentifier sender = SJade.convertAIDtoFipa(msg.getSender());
		AgentAction request = (AgentAction)Nuggets.objectFromXML(msg.getContent());
		boolean processed = false;
		int i = tooladapters.size();
		while(i>0)
		{
			IToolAdapter adapter = (IToolAdapter)tooladapters.get(--i);
			if(adapter.getMessageClass().isInstance(request))
			{
				try
				{
					adapter.handleToolRequest(sender, request, new JadeToolReply(msg));
					processed = true;
				}
				catch(RuntimeException e)
				{
					//agent.getLogger().severe("Tool adapter " + adapter + "threw exception " + e);
					e.printStackTrace();
				}
			}
		}
		if(!processed)
		{
			//agent.getLogger().warning("No tool adapter to handle: " + request);
		}

	}*/

	/*private final class JadeToolReply implements IToolReply
	{
		private final ACLMessage	msg;

		private JadeToolReply(ACLMessage msg)
		{
			super();
			this.msg = msg;
		}

		public void sendInform(Object content, boolean sync)
		{
			sendNative(ACLMessage.INFORM, content, sync);
		}

		public void sendFailure(Object content, boolean sync)
		{
			sendNative(ACLMessage.FAILURE, content, sync);
		}
		
		public void	cleanup()
		{
			// Todo: How to interrupt blocking receive?
		}

		protected void sendNative(int performative, Object content, boolean sync)
		{			
			ACLMessage reply = this.msg.createReply();
			reply.setPerformative(performative);
			reply.setContent(Nuggets.objectToXML(content));
			myAgent.send(reply);
			if(sync)
			{
				// Hack!!! Shouldn't use blockingReceive.
				// Todo: wait also for failure from AMS
				// todo: make timeout explicit
				ACLMessage rcv = myAgent.blockingReceive(MessageTemplate.and(MessageTemplate
						.MatchSender(this.msg.getSender()), MessageTemplate
						.MatchPerformative(ACLMessage.INFORM)), 10000);
				if(rcv == null)
				{
					throw new TimeoutException("Cannot send message to " + this.msg.getSender());
				}
			}
		}
	}*/
}
