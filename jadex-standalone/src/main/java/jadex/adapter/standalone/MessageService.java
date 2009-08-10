package jadex.adapter.standalone;

import jadex.adapter.base.DefaultResultListener;
import jadex.adapter.base.execution.IExecutionService;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.standalone.transport.ITransport;
import jadex.bridge.AgentTerminatedException;
import jadex.bridge.ContentException;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IClockService;
import jadex.bridge.IContentCodec;
import jadex.bridge.ILibraryService;
import jadex.bridge.IMessageService;
import jadex.bridge.MessageFailureException;
import jadex.bridge.MessageType;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IResultListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 *  The Message service serves several message-oriented purposes: a) sending and
 *  delivering messages by using transports b) management of transports
 *  (add/remove)
 *  
 *  The message service performs sending and delivering messages by separate actions
 *  that are individually executed on the execution service, i.e. they are delivered
 *  synchronous or asynchronous depending on the execution service mode.
 */
public class MessageService implements IMessageService
{
	//-------- constants --------
	
	/** The default codecs. */
	protected static IContentCodec[]	DEFCODECS	= new IContentCodec[]
	{
		new jadex.adapter.base.JavaXMLContentCodec(),
		new jadex.adapter.base.JadexXMLContentCodec(),
		new jadex.adapter.base.NuggetsXMLContentCodec()
	};
	
	//-------- attributes --------

	/** The ams. */
	protected AbstractPlatform platform;

	/** The transports. */
	protected List transports;

	/** All addresses of this platform. */
	private String[] addresses;

	/** The send message action executed by platform executor. */
	protected SendMessage sendmsg;
	
	/** The deliver message action executed by platform executor. */
	protected DeliverMessage delivermsg;
	
	/** The logger. */
	protected Logger logger;

	//-------- constructors --------

	/**
	 *  Constructor for Outbox.
	 *  @param platform
	 */
	public MessageService(AbstractPlatform platform, ITransport[] transports)
	{
		this.platform = platform;
		this.transports = SCollection.createArrayList();
		for(int i=0; i<transports.length; i++)
			this.transports.add(transports[i]);
		this.sendmsg = new SendMessage();
		this.delivermsg = new DeliverMessage();
		this.logger = Logger.getLogger("MessageService" + this);
	}
	
	//-------- interface methods --------

	/**
	 *  Send a message.
	 *  @param message The native message.
	 */
	public void sendMessage(Map message, MessageType type, IAgentIdentifier sender, ClassLoader cl)
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

		sendmsg.addMessage(message, type.getName(), receivers);
	}

	/**
	 *  Deliver a message to the intended agents. Called from transports.
	 *  @param message The native message. 
	 *  (Synchronized because can be called from concurrently executing transports)
	 */
	public synchronized void deliverMessage(Map message, String msgtype, IAgentIdentifier[] receivers)
	{	
//		internalDeliverMessage(message);
		delivermsg.addMessage(message, msgtype, receivers);
	}

	/**
	 *  Adds a transport for this outbox.
	 *  @param transport The transport.
	 */
	public void addTransport(ITransport transport)
	{
		transports.add(transport);
		addresses = null;
	}

	/**
	 *  Remove a transport for the outbox.
	 *  @param transport The transport.
	 */
	public void removeTransport(ITransport transport)
	{
		transports.remove(transport);
		transport.shutdown();
		addresses = null;
	}

	/**
	 *  Moves a transport up or down.
	 *  @param up Move up?
	 *  @param transport The transport to move.
	 */
	public synchronized void changeTransportPosition(boolean up, ITransport transport)
	{
		int index = transports.indexOf(transport);
		if(up && index>0)
		{
			ITransport temptrans = (ITransport)transports.get(index - 1);
			transports.set(index - 1, transport);
			transports.set(index, temptrans);
		}
		else if(index!=-1 && index<transports.size()-1)
		{
			ITransport temptrans = (ITransport)transports.get(index + 1);
			transports.set(index + 1, transport);
			transports.set(index, temptrans);
		}
		else
		{
			throw new RuntimeException("Cannot change transport position from "
				+index+(up? " up": " down"));
		}
	}

	/**
	 *  Get the adresses of an agent.
	 *  @return The addresses of this agent.
	 */
	public String[] getAddresses()
	{
		if(addresses == null)
		{
			ITransport[] trans = (ITransport[])transports.toArray(new ITransport[transports.size()]);
			ArrayList addrs = new ArrayList();
			for(int i = 0; i < trans.length; i++)
			{
				String[] traddrs = trans[i].getAddresses();
				for(int j = 0; j < traddrs.length; j++)
					addrs.add(traddrs[j]);
			}
			addresses = (String[])addrs.toArray(new String[addrs.size()]);
		}

		return addresses;
	}

	/**
	 *  Get the transports.
	 *  @return The transports.
	 */
	public ITransport[] getTransports()
	{
		ITransport[] transportsArray = new ITransport[transports.size()];
		return (ITransport[])transports.toArray(transportsArray);
	}

	//-------- IPlatformService interface --------
	
	/**
	 *  Start the service.
	 */
	public void start()
	{
		ITransport[] tps = (ITransport[])transports.toArray(new ITransport[transports.size()]);
		for(int i=0; i<tps.length; i++)
		{
			try
			{
				tps[i].start();
			}
			catch(Exception e)
			{
				System.out.println("Could not initialize transport: "+tps[i]+" reason: "+e);
				transports.remove(tps[i]);
			}
		}
		
		if(transports.size()==0)
			throw new RuntimeException("MessageService has no working transport for sending messages.");
	}
	
	/**
	 *  Called when the platform shuts down. Do necessary cleanup here (if any).
	 */
	public void shutdown(IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		for(int i = 0; i < transports.size(); i++)
		{
			((ITransport)transports.get(i)).shutdown();
		}
		
		listener.resultAvailable(null);
	}

	// -------- internal methods --------

	/**
	 *  Send a message.
	 *  @param message The native message.
	 */
	protected void internalSendMessage(Map msg, String type, IAgentIdentifier[] receivers)
	{
//		IAgentIdentifier[] receivers = message.getReceivers();
		if(receivers.length == 0)
			throw new MessageFailureException(msg, "No receiver specified");
		for(int i=0; i<receivers.length; i++)
		{
			if(receivers[i]==null)
				throw new MessageFailureException(msg, "A receiver nulls");
		}

		ITransport[] trans = (ITransport[])transports.toArray(new ITransport[transports.size()]);

		for(int i = 0; i < trans.length; i++)
		{
			try
			{
				// Method returns agent identifiers of undelivered agents
				receivers = trans[i].sendMessage(msg, type, receivers);
			}
			catch(Exception e)
			{
				// todo:
				e.printStackTrace();
			}
		}

		if(receivers.length > 0)
			logger.warning("Message could not be delivered to (all) receivers: " + SUtil.arrayToString(receivers));
		// throw new MessageFailureException(message, "Message could not be
		// delivered to all receivers: "
		// +SUtil.arrayToString(receivers));
	}
	
	/**
	 *  Deliver a message to the receivers.
	 */
	protected void internalDeliverMessage(final Map msg, final String type, final IAgentIdentifier[] receivers)
	{
		final MessageType	messagetype	= platform.getMessageType(type);
		final Map	decoded	= new HashMap();	// Decoded messages cached by class loader to avoid decoding the same message more than once, when the same class loader is used.
		
		for(int i = 0; i < receivers.length; i++)
		{
			((IAMS)platform.getService(IAMS.class)).getAgentAdapter(receivers[i], new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					StandaloneAgentAdapter agent = (StandaloneAgentAdapter)result;
					if(agent != null)
					{
						ClassLoader cl = agent.getKernelAgent().getClassLoader();
						Map	message	= (Map) decoded.get(cl);
						if(message==null)
						{
							if(receivers.length>1)
							{
								message	= new HashMap(msg);
								decoded.put(cl, message);
							}
							else
							{
								// Skip creation of copy when only one receiver.
								message	= msg;
							}

							// Conversion via platform specific codecs
							for(Iterator it=message.keySet().iterator(); it.hasNext(); )
							{
								String	name	= (String)it.next();
								Object	value	= message.get(name);
								IContentCodec	codec	= messagetype.findContentCodec(DEFCODECS, message, name);
								if(codec!=null)
								{
									message.put(name, codec.decode((String)value, cl));
								}
							}
						}

						try
						{
							agent.receiveMessage(message, messagetype);
						}
						catch(AgentTerminatedException ate)
						{
							logger.warning("Message could not be delivered to receiver(s): " + message);

							// todo: notify sender that message could not be delivered!
							// Problem: there is no connection back to the sender, so that
							// the only chance is sending a separate failure message.
						}
					}
					else
					{
						logger.warning("Message could not be delivered to receiver(s): " + msg);

						// todo: notify sender that message could not be delivered!
						// Problem: there is no connection back to the sender, so that
						// the only chance is sending a separate failure message.
					}
				}

				public void exceptionOccurred(Exception exception)
				{
					logger.severe("Exception occurred: "+exception);
				}
			});
			
		}
	}
	
	/**
	 *  Send message(s) executable.
	 */
	protected class SendMessage implements IExecutable
	{
		//-------- attributes --------
		
		/** The list of messages to send. */
		protected List messages;
		
		//-------- constructors --------
		
		/**
		 *  Create a new send message executable.
		 */
		public SendMessage()
		{
			this.messages = new ArrayList();
		}
		
		//-------- methods --------
		
		/**
		 *  Send a message.
		 */
		public synchronized boolean execute()
		{
			if(!messages.isEmpty())
			{
				Object[] tmp = (Object[])messages.remove(0);
				internalSendMessage((Map)tmp[0], (String)tmp[1], (IAgentIdentifier[])tmp[2]);
			}
			return !messages.isEmpty();
		}
		
		/**
		 *  Add a message to be sent.
		 *  @param message The message.
		 */
		public synchronized void addMessage(Map message, String type, IAgentIdentifier[] receivers)
		{
			messages.add(new Object[]{message, type, receivers});
			((IExecutionService)platform.getService(IExecutionService.class)).execute(this);
		}
	}
	
	/**
	 *  Deliver message(s) executable.
	 */
	protected class DeliverMessage implements IExecutable
	{
		//-------- attributes --------
		
		/** The list of messages to send. */
		protected List messages;
		
		//-------- constructors --------
		
		/**
		 *  Create a new deliver message executable.
		 */
		public DeliverMessage()
		{
			this.messages = new ArrayList();
		}
		
		//-------- methods --------
		
		/**
		 *  Deliver the message.
		 */
		public synchronized boolean execute()
		{
			if(!messages.isEmpty())
			{
				Object[] tmp = (Object[])messages.remove(0);
				internalDeliverMessage((Map)tmp[0], (String)tmp[1], (IAgentIdentifier[])tmp[2]);
			}
			return !messages.isEmpty();
		}
		
		/**
		 *  Add a message to be delivered.
		 */
		public synchronized void addMessage(Map message, String type, IAgentIdentifier[] receivers)
		{
			messages.add(new Object[]{message, type, receivers});
			((IExecutionService)platform.getService(IExecutionService.class)).execute(this);
		}
	}
}


