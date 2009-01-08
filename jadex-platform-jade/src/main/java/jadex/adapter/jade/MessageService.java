package jadex.adapter.jade;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jadex.adapter.base.fipa.AMSCreateAgent;
import jadex.adapter.base.fipa.AMSDestroyAgent;
import jadex.adapter.base.fipa.AMSResumeAgent;
import jadex.adapter.base.fipa.AMSSearchAgents;
import jadex.adapter.base.fipa.AMSShutdownPlatform;
import jadex.adapter.base.fipa.AMSStartAgent;
import jadex.adapter.base.fipa.AMSSuspendAgent;
import jadex.adapter.base.fipa.DFDeregister;
import jadex.adapter.base.fipa.DFModify;
import jadex.adapter.base.fipa.DFRegister;
import jadex.adapter.base.fipa.DFSearch;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IDFAgentDescription;
import jadex.adapter.base.fipa.SFipa;
import jadex.bridge.ContentException;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IClockService;
import jadex.bridge.IContentCodec;
import jadex.bridge.ILibraryService;
import jadex.bridge.IMessageService;
import jadex.bridge.MessageType;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IResultListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;


/**
 *  The Message service serves several message-oriented purposes: a) sending and
 *  delivering messages by using transports 
 */
public class MessageService implements IMessageService
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
	public void sendMessage(Map message, MessageType type, IAgentIdentifier sender)
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
		
		IAgentIdentifier[] receivers = null;
		Object tmp = message.get(type.getReceiverIdentifier());
		if(tmp instanceof Collection)
			receivers = (IAgentIdentifier[])((Collection)tmp).toArray(new IAgentIdentifier[0]);
		else
			receivers = (IAgentIdentifier[])tmp;
		
		if(receivers==null || receivers==new IAgentIdentifier[0])
		{
			throw new RuntimeException("Receivers must not be empty: "+message);
		}

		// Hack!!! Convert Jadex/Nuggets AMS/DF messages to FIPA.
		if(type.equals(SFipa.FIPA_MESSAGE_TYPE))
		{
			if(SFipa.AGENT_MANAGEMENT_ONTOLOGY_NAME.equals(message.get(SFipa.ONTOLOGY)))
			{
				IDFAgentDescription	dfadesc	= null;
				if(message.get(SFipa.CONTENT) instanceof DFRegister)
					dfadesc	= ((DFRegister)message.get(SFipa.CONTENT)).getAgentDescription();
				else if(message.get(SFipa.CONTENT) instanceof DFModify)
					dfadesc	= ((DFModify)message.get(SFipa.CONTENT)).getAgentDescription();
				else if(message.get(SFipa.CONTENT) instanceof DFSearch)
					dfadesc	= ((DFSearch)message.get(SFipa.CONTENT)).getAgentDescription();
				else if(message.get(SFipa.CONTENT) instanceof DFDeregister)
					dfadesc	= ((DFDeregister)message.get(SFipa.CONTENT)).getAgentDescription();
				
				if(dfadesc!=null)
				{
					DFAgentDescription	dfadesc_jade	= SJade.convertAgentDescriptiontoJade(dfadesc);
				}
			}
			
			if(message.get(SFipa.CONTENT) instanceof AMSCreateAgent
				|| message.get(SFipa.CONTENT) instanceof AMSStartAgent
				|| message.get(SFipa.CONTENT) instanceof AMSDestroyAgent
				|| message.get(SFipa.CONTENT) instanceof AMSSuspendAgent
				|| message.get(SFipa.CONTENT) instanceof AMSResumeAgent
				|| message.get(SFipa.CONTENT) instanceof AMSSearchAgents
				|| message.get(SFipa.CONTENT) instanceof AMSShutdownPlatform)
			{
				throw new RuntimeException("Not yet supported.");
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
				// todo: use agent specific classloader
				ClassLoader cl = ((ILibraryService)platform.getService(ILibraryService.class)).getClassLoader();
				message.put(name, codec.encode(value, cl));
			}
			else if(value!=null && !(value instanceof String) 
				&& !(name.equals(type.getSenderIdentifier()) || name.equals(type.getReceiverIdentifier())))
			{	
				throw new ContentException("No content codec found for: "+name+", "+message);
			}
		}

		// Prepare message for Jade.
		if(!type.getName().equals(SFipa.MESSAGE_TYPE_NAME_FIPA))
			throw new RuntimeException("Only message type FIPA supported using JADE infrastructure.");
		
		final ACLMessage msg = new ACLMessage(SJade.convertPerformativetoJade((String)message.get(SFipa.PERFORMATIVE)));
		for(int i=0; i<receivers.length; i++)
		{
			msg.addReceiver(SJade.convertAIDtoJade(receivers[i]));
		}
		msg.setContent((String)message.get(SFipa.CONTENT));
		msg.setConversationId((String)message.get(SFipa.CONVERSATION_ID));
		msg.setReplyWith((String)message.get(SFipa.REPLY_WITH));
		msg.setInReplyTo((String)message.get(SFipa.IN_REPLY_TO));
		msg.setLanguage((String)message.get(SFipa.LANGUAGE));
		msg.setOntology((String)message.get(SFipa.ONTOLOGY));
		msg.setProtocol((String)message.get(SFipa.PROTOCOL));
		msg.setSender(SJade.convertAIDtoJade((IAgentIdentifier)message.get(SFipa.SENDER)));
		msg.setEncoding((String)message.get(SFipa.ENCODING));
		
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
	public synchronized void deliverMessage(Map message, String msgtype, IAgentIdentifier[] receivers)
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
	public void start()
	{
	}
	
	/**
	 *  Called when the platform shuts down. Do necessary cleanup here (if any).
	 */
	public void shutdown(IResultListener listener)
	{
	}
}

