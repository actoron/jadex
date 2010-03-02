package jadex.standalone;

import jadex.base.DefaultResultListener;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.ContentException;
import jadex.bridge.DefaultMessageAdapter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IContentCodec;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.IMessageListener;
import jadex.bridge.IMessageService;
import jadex.bridge.MessageFailureException;
import jadex.bridge.MessageType;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IResultListener;
import jadex.service.IService;
import jadex.service.clock.IClockService;
import jadex.service.execution.IExecutionService;
import jadex.standalone.service.ComponentManagementService;
import jadex.standalone.transport.ITransport;

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
public class MessageService implements IMessageService, IService
{
	//-------- constants --------
	
	/** The default codecs. */
    protected static IContentCodec[] DEFCODECS = new IContentCodec[]
    {
        new jadex.base.JavaXMLContentCodec(),
        new jadex.base.JadexXMLContentCodec(),
        new jadex.base.NuggetsXMLContentCodec()
    };
	
	//-------- attributes --------

	/** The ams. */
	protected AbstractPlatform platform;

	/** The transports. */
	protected List transports;

	/** All addresses of this platform. */
	private String[] addresses;

	/** The message types. */
	protected Map messagetypes;
	
	/** The send message action executed by platform executor. */
	protected SendMessage sendmsg;
	
	/** The deliver message action executed by platform executor. */
	protected DeliverMessage delivermsg;
	
	/** The logger. */
	protected Logger logger;
	
	/** The listeners. */
	protected List listeners;

	//-------- constructors --------

	/**
	 *  Constructor for Outbox.
	 *  @param platform
	 */
	public MessageService(AbstractPlatform platform, ITransport[] transports, MessageType[] messagetypes)
	{
		this.platform = platform;
		this.transports = SCollection.createArrayList();
		for(int i=0; i<transports.length; i++)
			this.transports.add(transports[i]);
		this.messagetypes	= SCollection.createHashMap();
		for(int i=0; i<messagetypes.length; i++)
			this.messagetypes.put(messagetypes[i].getName(), messagetypes[i]);		
		this.sendmsg = new SendMessage();
		this.delivermsg = new DeliverMessage();
		this.logger = Logger.getLogger("MessageService" + this);
	}
	
	//-------- interface methods --------

	/**
	 *  Send a message.
	 *  @param message The native message.
	 */
	public void sendMessage(Map msg, MessageType type, IComponentIdentifier sender, ClassLoader cl)
	{
		if(sender==null)
			throw new RuntimeException("Sender must not be null: "+msg);
	
		Map msgcopy = new HashMap(msg);
		
		// Automatically add optional meta information.
		String senid = type.getSenderIdentifier();
		Object sen = msgcopy.get(senid);
		if(sen==null)
			msgcopy.put(senid, sender);
		
		String idid = type.getIdIdentifier();
		Object id = msgcopy.get(idid);
		if(id==null)
			msgcopy.put(idid, SUtil.createUniqueId(sender.getLocalName()));

		String sd = type.getTimestampIdentifier();
		Object senddate = msgcopy.get(sd);
		if(senddate==null)
		{
			IClockService	clock	= (IClockService) platform.getService(IClockService.class);
			if(clock!=null)
				msgcopy.put(sd, ""+clock.getTime());
		}
		
		IComponentIdentifier[] receivers = null;
		Object tmp = msgcopy.get(type.getReceiverIdentifier());
		if(tmp instanceof Collection)
			receivers = (IComponentIdentifier[])((Collection)tmp).toArray(new IComponentIdentifier[0]);
		else if(tmp.getClass().isArray())
			receivers = (IComponentIdentifier[])tmp;
		else if(tmp instanceof IComponentIdentifier)
			receivers = new IComponentIdentifier[]{(IComponentIdentifier)tmp};
		
		if(receivers==null || receivers==new IComponentIdentifier[0])
		{
			throw new RuntimeException("Receivers must not be empty: "+msgcopy);
		}

		// Conversion via platform specific codecs
		for(Iterator it=msgcopy.keySet().iterator(); it.hasNext(); )
		{
			String	name	= (String)it.next();
			Object	value	= msgcopy.get(name);
			IContentCodec	codec	= type.findContentCodec(DEFCODECS, msgcopy, name);
			if(codec!=null)
			{
				msgcopy.put(name, codec.encode(value, cl));
			}
			else if(value!=null && !(value instanceof String) 
				&& !(name.equals(type.getSenderIdentifier()) || name.equals(type.getReceiverIdentifier())))
			{	
				throw new ContentException("No content codec found for: "+name+", "+msgcopy);
			}
		}

		if(listeners!=null)
		{
			// Hack?!
			IMessageAdapter msgadapter = new DefaultMessageAdapter(msgcopy, type);
			for(int i=0; i<listeners.size(); i++)
			{
				IMessageListener lis = (IMessageListener)listeners.get(i);
				lis.messageSent(msgadapter);
			}
		}
		
		sendmsg.addMessage(msgcopy, type.getName(), receivers);
	}

	/**
	 *  Deliver a message to the intended components. Called from transports.
	 *  @param message The native message. 
	 *  (Synchronized because can be called from concurrently executing transports)
	 */
	public synchronized void deliverMessage(Map message, String msgtype, IComponentIdentifier[] receivers)
	{	
//		internalDeliverMessage(message);
		
		if(listeners!=null)
		{
			// Hack?!
			IMessageAdapter msg = new DefaultMessageAdapter(message, getMessageType(msgtype));
			for(int i=0; i<listeners.size(); i++)
			{
				IMessageListener lis = (IMessageListener)listeners.get(i);
				lis.messageReceived(msg);
			}
		}
		
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
	 *  Get the adresses of a component.
	 *  @return The addresses of this component.
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
	public void startService()
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
	public void shutdownService(IResultListener listener)
	{
		if(listener==null)
			listener = DefaultResultListener.getInstance();
		
		for(int i = 0; i < transports.size(); i++)
		{
			((ITransport)transports.get(i)).shutdown();
		}
		
		listener.resultAvailable(this, null);
	}

	/**
	 *  Get the message type.
	 *  @param type The type name.
	 *  @return The message type.
	 */
	public MessageType getMessageType(String type)
	{
		return (MessageType)messagetypes.get(type);
	}
	
	/**
	 *  Add a message listener.
	 *  @param listener The change listener.
	 */
	public synchronized void addMessageListener(IMessageListener listener)
	{
		if(listeners==null)
			listeners = new ArrayList();
		listeners.add(listener);
	}
	
	/**
	 *  Remove a message listener.
	 *  @param listener The change listener.
	 */
	public synchronized void removeMessageListener(IMessageListener listener)
	{
		listeners.remove(listener);
	}
	
	//-------- internal methods --------

	/**
	 *  Send a message.
	 *  @param message The native message.
	 */
	protected void internalSendMessage(Map msg, String type, IComponentIdentifier[] receivers)
	{
//		IComponentIdentifier[] receivers = message.getReceivers();
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
				// Method returns component identifiers of undelivered components
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
	protected void internalDeliverMessage(final Map msg, final String type, final IComponentIdentifier[] receivers)
	{
		final MessageType	messagetype	= getMessageType(type);
		final Map	decoded	= new HashMap();	// Decoded messages cached by class loader to avoid decoding the same message more than once, when the same class loader is used.
		
		for(int i = 0; i < receivers.length; i++)
		{
			((ComponentManagementService)platform.getService(IComponentManagementService.class)).getComponentAdapter(receivers[i], new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					StandaloneComponentAdapter component = (StandaloneComponentAdapter)result;
					if(component != null)
					{
						ClassLoader cl = component.getComponentInstance().getClassLoader();
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
							component.receiveMessage(message, messagetype);
						}
						catch(ComponentTerminatedException ate)
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

				public void exceptionOccurred(Object source, Exception exception)
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
				internalSendMessage((Map)tmp[0], (String)tmp[1], (IComponentIdentifier[])tmp[2]);
			}
			return !messages.isEmpty();
		}
		
		/**
		 *  Add a message to be sent.
		 *  @param message The message.
		 */
		public synchronized void addMessage(Map message, String type, IComponentIdentifier[] receivers)
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
				internalDeliverMessage((Map)tmp[0], (String)tmp[1], (IComponentIdentifier[])tmp[2]);
			}
			return !messages.isEmpty();
		}
		
		/**
		 *  Add a message to be delivered.
		 */
		public synchronized void addMessage(Map message, String type, IComponentIdentifier[] receivers)
		{
			messages.add(new Object[]{message, type, receivers});
			((IExecutionService)platform.getService(IExecutionService.class)).execute(this);
		}
	}
}


