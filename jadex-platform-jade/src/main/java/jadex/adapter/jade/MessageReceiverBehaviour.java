package jadex.adapter.jade;

import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.FIPAManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.MessageTemplate.MatchExpression;
import jadex.adapter.base.fipa.FIPAMessageType;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.adapter.jade.fipaimpl.AgentIdentifier;
import jadex.bridge.DefaultMessageAdapter;
import jadex.bridge.IContentCodec;
import jadex.bridge.IKernelAgent;
import jadex.bridge.ILibraryService;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.MessageType;
import jadex.commons.collection.SCollection;
import nuggets.Nuggets;

import java.util.Iterator;
import java.util.List;


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
				
				JadeMessageAdapter ma = new JadeMessageAdapter(msg, ams);
				
				// Conversion via platform specific codecs
				String[] params = ma.getMessageType().getParameterNames();
				for(int i=0; i<params.length; i++)
				{
					IContentCodec codec = ma.getMessageType().findContentCodec(DEFCODECS, ma, params[i]);
					if(codec!=null)
					{
						// todo: use agent specific classloader
						ClassLoader cl = ((ILibraryService)platform.getService(ILibraryService.class)).getClassLoader();
						ma.setDecodedValue(params[i], codec.decode((String)ma.getValue(params[i]), cl));
					}
				}
				// todo: sets?
				
				// Hack!!! Convert FIPA AMS/DF messages to Jadex/Nuggets
				if(ma.getMessageType().equals(SFipa.FIPA_MESSAGE_TYPE))
				{
					if(FIPAManagementOntology.NAME.equals(ma.getValue(SFipa.ONTOLOGY)))
					{
						// todo...
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
