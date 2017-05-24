package jadex.bridge.component.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.ServiceNotFoundException;

import jadex.base.PlatformConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMessageHandler;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.bridge.service.types.platformstate.IPlatformStateService;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.traverser.SCloner;

/**
 *  Feature to send messages and receive messages via handlers.
 *  Also implements reacting to incoming stream connections (only exposed in micro agents for now).
 */
public class MessageComponentFeature extends AbstractComponentFeature implements IMessageFeature, IInternalMessageFeature
{
	/** Message header key for the sender. */
	public static final String SENDER = "sender";
	
	/** Message header key for the receiver. */
	public static final String RECEIVER = "receiver";
	
	/** Message header key for the conversation ID. */
	public static final String CONVERSATION_ID = "convid";
	
	//-------- attributes --------
	
	/** The platform ID. */
	protected IComponentIdentifier platformid;
	
	/** Platform state service. */
	protected IPlatformStateService pfstate;
	
	/** The list of message handlers. */
	protected List<IMessageHandler> messagehandlers;
	
	/** The security service. */
	protected ISecurityService secservice;
	
	/** Flag whether to allow receiving untrusted messages. */
	protected boolean allowuntrusted;
	
	/** Messages awaiting reply. */
	protected Map<String, Future<Object>> awaitingmessages;
	
	//-------- constructors --------
	
	/**
	 *  Create the feature.
	 */
	public MessageComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		platformid = component.getComponentIdentifier().getRoot();
	}
	
	/**
	 *  Check if the feature potentially executed user code in body.
	 *  Allows blocking operations in user bodies by using separate steps for each feature.
	 *  Non-user-body-features are directly executed for speed.
	 *  If unsure just return true. ;-)
	 */
	public boolean	hasUserBody()
	{
		return false;
	}
	
	//-------- IMessageFeature interface --------
	
	/**
	 *  Send a message.
	 *  @param receiver	The message receiver.
	 *  @param message	The message.
	 *  
	 */
	public IFuture<Void> sendMessage(final IComponentIdentifier receiver, Object message)
	{
		if (receiver == null)
			return new Future<Void>(new IllegalArgumentException("Messages must have a receiver."));
		
		final Future<Void> ret = new Future<Void>();
		
		final Map<String, Object> header = new HashMap<String, Object>();
		header.put(SENDER, component.getComponentIdentifier());
		header.put(RECEIVER, receiver);
		
		if (receiver.getRoot().equals(platformid))
		{
			// Direct local delivery.
			ClassLoader cl = SComponentManagementService.getLocalClassLoader(receiver);
			final Object clonedmsg = SCloner.clone(message, cl);
			
			SComponentManagementService.getLocalExternalAccess(receiver).scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					IInternalMessageFeature imf = ia.getComponentFeature0(IInternalMessageFeature.class);
					
					if (imf != null)
					{
						imf.messageArrived(null, header, clonedmsg);
						return IFuture.DONE;
					}
					
					return new Future<Void>(new RuntimeException("Receiver " + ia.getComponentIdentifier() + " has no messaging."));
				}
			}).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ISerializationServices serialserv = getPlatformStateService().getSerializationServices();
			byte[] body = serialserv.encode(header, component.getClassLoader(), message);
			getSecurityService().encryptAndSign(header, body).addResultListener(new ExceptionDelegationResultListener<byte[], Void>((Future<Void>) ret)
			{
				public void customResultAvailable(final byte[] body) throws Exception
				{
					sendToTransports(header, body).addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Send a message and wait for a reply.
	 *  
	 *  @param receiver	The message receiver.
	 *  @param message	The message.
	 *  
	 *  @return The reply.
	 */
	public IFuture<Object> sendMessageAndWait(IComponentIdentifier receiver, Object message)
	{
		return sendMessageAndWait(receiver, message, null);
	}
	
	/**
	 *  Send a message and wait for a reply.
	 *  
	 *  @param receiver	The message receiver.
	 *  @param message	The message.
	 *  @param timeout	The reply timeout.
	 *  
	 *  @return The reply.
	 */
	public IFuture<Object> sendMessageAndWait(IComponentIdentifier receiver, Object message, Long timeout)
	{
		final Future<Object> ret = new Future<Object>();
		final String convid = SUtil.createUniqueId(component.getComponentIdentifier().toString());
		WaitingMessageWrapper wms = new WaitingMessageWrapper(convid, message);
		if (awaitingmessages == null)
			awaitingmessages = new HashMap<String, Future<Object>>();
		awaitingmessages.put(convid, ret);
		sendMessage(receiver, wms).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				// NOP
			}
			
			public void exceptionOccurred(Exception exception)
			{
				Future<Object> fut = awaitingmessages.remove(convid);
				if (fut != null)
					fut.setException(exception);
			}
		});
		timeout = timeout == null ? PlatformConfiguration.getLocalDefaultTimeout(platformid) : timeout;
		component.getComponentFeature0(IExecutionFeature.class).waitForDelay(timeout, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				Future<Object> fut = awaitingmessages.remove(convid);
				if (fut != null)
				{
					fut.setException(new TimeoutException("Failed to receive reply for message awaiting reply: " + convid));
				}
				return IFuture.DONE;
			}
		});
		return ret;
	}
	
	/**
	 *  Send a message reply.
	 *  @param receivedmessageid	ID of the received message that is being replied to.
	 *  @param message	The reply message.
	 *  
	 */
	public IFuture<Void> sendReply(Object receivedmessageid, Object message)
	{
		if (!(receivedmessageid instanceof Map))
			return new Future<Void>(new IllegalArgumentException("Cannot reply, illegal message ID or null."));
		
		@SuppressWarnings("unchecked")
		Map<String, Object> oldmsgheader = (Map<String, Object>) receivedmessageid;
		IComponentIdentifier rplyrec = (IComponentIdentifier) oldmsgheader.get(SENDER);
		String convid = (String) oldmsgheader.get(CONVERSATION_ID);
		if (rplyrec == null)
			return new Future<Void>(new IllegalArgumentException("Cannot reply, reply receiver ID not found."));
		if (convid == null)
			return new Future<Void>(new IllegalArgumentException("Cannot reply, conversation ID not found."));
		
		Map<String, Object> header = new HashMap<String, Object>();
		header.put(RECEIVER, rplyrec);
		header.put(SENDER, component.getComponentIdentifier());
		WaitingMessageWrapper wms = new WaitingMessageWrapper(convid, message);
		wms.setReply(true);
		
		return sendMessage(rplyrec, wms);
	}
	
	/**
	 *  Forwards the prepared message to the transport layer.
	 *  
	 *  @param header The message header.
	 *  @param encryptedbody The encrypted message body.
	 *  @return Null, when done, exception if failed.
	 */
	public IFuture<Void> sendToTransports(final Map<String, Object> header, final byte[] encryptedbody)
	{
		final Future<Void> ret = new Future<Void>();
		getTransportService(header).addResultListener(new ExceptionDelegationResultListener<ITransportService, Void>(ret)
		{
			public void customResultAvailable(ITransportService result) throws Exception
			{
				result.sendMessage(header, encryptedbody).addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						ret.setResult(null);
					};
					
					public void exceptionOccurred(Exception exception)
					{
						// Flush cache, this may cause jitter due lack of synchronization, but should eventually recover.
						IComponentIdentifier rplat = ((IComponentIdentifier) header.get(RECEIVER)).getRoot();
						getPlatformStateService().getTransportCache().remove(rplat);
						
						getTransportService(header).addResultListener(new IResultListener<ITransportService>()
						{
							public void resultAvailable(ITransportService result)
							{
								// Adding to cache done by select function.
								result.sendMessage(header, encryptedbody).addResultListener(new DelegationResultListener<Void>(ret));
							}
							
							public void exceptionOccurred(Exception exception)
							{
								ret.setException(exception);
							}
						});
					};
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Add a message handler.
	 *  @param  The handler.
	 */
	public IFuture<Void> addMessageHandler(final IMessageHandler handler)
	{
		messagehandlers.add(handler);
		
		return IFuture.DONE;
	}
	
	/**
	 *  Remove a message handler.
	 *  @param handler The handler.
	 */
	public IFuture<Void> removeMessageHandler(IMessageHandler handler)
	{
		if(messagehandlers!=null)
		{
			messagehandlers.remove(handler);
		}
		return IFuture.DONE;
	}
	
	//-------- IInternalMessageFeature interface --------
	
	/**
	 *  Inform the component that a message has arrived.
	 *  
	 *  @param header The message header.
	 *  @param bodydata The encrypted message that arrived.
	 */
	public void messageArrived(final Map<String, Object> header, final byte[] bodydata)
	{
		if (header != null && bodydata != null)
		{
			getSecurityService().decryptAndAuth((IComponentIdentifier) header.get(SENDER), bodydata).addResultListener(new IResultListener<Tuple2<IMsgSecurityInfos,byte[]>>()
			{
				public void resultAvailable(Tuple2<IMsgSecurityInfos, byte[]> result)
				{
					// Check if SecurityService ok'd it at all.
					if (result != null)
					{
						final IMsgSecurityInfos secinf = result.getFirstEntity();
						
						// Only accept messages we trust.
						if (secinf.isTrustedPlatform() || allowuntrusted)
						{
							Object body = getPlatformStateService().getSerializationServices().decode(component.getClassLoader(), bodydata);
							messageArrived(secinf, header, body);
						}
					}
				};
				
				public void exceptionOccurred(Exception exception)
				{
				}
			});
		}
	}
	
	/**
	 *  Inform the component that a message has arrived.
	 *  
	 *  @param secinfos The security meta infos.
	 *  @param header The message header.
	 *  @param body The message that arrived.
	 */
	public void messageArrived(final IMsgSecurityInfos secinfos, final Map<String, Object> header, Object body)
	{
		if (body instanceof WaitingMessageWrapper)
		{
			final WaitingMessageWrapper wm = (WaitingMessageWrapper) body;
			
			if (wm.isReply())
			{
				Future<Object> fut = awaitingmessages.remove(wm.getConversationId());
				if (fut != null)
					fut.setResult(wm.getUserMessage());
				return;
			}
			
			body = wm.getUserMessage();
			header.put(CONVERSATION_ID, wm.getConversationId());
		}
		
		handleMessage(secinfos, header, body);
	}
	
	/**
	 *  Handle message with user message handlers.
	 *  
	 *  @param secinf Security meta infos.
	 *  @param header Message header.
	 * @param body
	 */
	public void handleMessage(final IMsgSecurityInfos secinf, final Map<String, Object> header, final Object body)
	{
		for (Iterator<IMessageHandler> it = messagehandlers.iterator(); it.hasNext(); )
		{
			final IMessageHandler handler = it.next();
			if (handler.isRemove())
				it.remove();
			else if (handler.isHandling(secinf, header, body))
			{
				component.getComponentFeature0(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						handler.handleMessage(secinf, header, body);
						return IFuture.DONE;
					}
				});
			}
		}
	}

	/**
	 *  Inform the component that a stream has arrived.
	 *  @param con The stream that arrived.
	 */
//	public void streamArrived(IConnection con)
//	{
//		getComponent().getComponentFeature(IExecutionFeature.class)
//			.scheduleStep(createHandleStreamStep(con))
//			.addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//				// NOP
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				// Todo: fail fast components?
//				StringWriter	sw	= new StringWriter();
//				exception.printStackTrace(new PrintWriter(sw));
//				getComponent().getLogger().severe("Exception during stream processing\n"+sw);
//			}
//		});
//	}
	
	/**
	 *  Find a suitable transport service for a message.
	 *  
	 *  @param header The message header.
	 *  @return A suitable transport service or exception if none is available.
	 */
	protected IFuture<ITransportService> getTransportService(Map<String, Object> header)
	{
		final Future<ITransportService> ret = new Future<ITransportService>();
		IComponentIdentifier rplat = ((IComponentIdentifier) header.get(RECEIVER)).getRoot();
		
		Tuple2<ITransportService, Integer> tup = getPlatformStateService().getTransportCache().get(rplat);
		if (tup != null)
		{
			getPlatformStateService().getTransportCache().put(rplat, tup);
		}
		else
		{
			final Collection<ITransportService> coll = SServiceProvider.getLocalServices(component, ITransportService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			if (coll != null && coll.size() > 0)
			{
				final IComponentIdentifier receiverplatform = ((IComponentIdentifier) header.get(RECEIVER)).getRoot();
				final int[] counter = new int[] { coll.size() };
				for (Iterator<ITransportService> it = coll.iterator(); it.hasNext(); )
				{
					final ITransportService tp = it.next();
					tp.isReady(header).addResultListener(new IResultListener<Integer>()
					{
						public void resultAvailable(Integer priority)
						{
							ret.setResultIfUndone(tp);
							Tuple2<ITransportService, Integer> tup = getPlatformStateService().getTransportCache().get(receiverplatform);
							if (tup == null || tup.getSecondEntity() < priority)
								getPlatformStateService().getTransportCache().put(receiverplatform, new Tuple2<ITransportService, Integer>(tp, priority));
						}
						
						public void exceptionOccurred(Exception exception)
						{
							--counter[0];
							if (counter[0] == 0)
							{
								String error = "Could not find working transport for receiver " + receiverplatform + ", tried:";
								for (ITransportService tp : coll)
								{
									error += " " + tp.toString();
								}
								ret.setException(new RuntimeException(error));
							}
						}
					});
				}
			}
			else
				ret.setException(new ServiceNotFoundException("No transport available."));
		}
		return ret;
	}
	
	/**
	 *  Gets the security service.
	 *  
	 *  @return The security service.
	 */
	protected ISecurityService getSecurityService()
	{
		if (secservice == null)
			secservice = SServiceProvider.getLocalService(component, ISecurityService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		return secservice;
	}
	
	/**
	 *  Returns the platform state service.
	 *  
	 *  @return Platform state service.
	 */
	protected IPlatformStateService getPlatformStateService()
	{
		if (pfstate == null)
			SServiceProvider.getLocalService(component, IPlatformStateService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		return pfstate;
	}
	
	/**
	 *  Creates a conversation ID.
	 *  
	 *  @return Large random conversation ID.
	 */
	protected static final long[] generateConversationId()
	{
		long[] convid = new long[4];
		for (int i = 0; i < convid.length; ++i)
			convid[i] = SUtil.SECURE_RANDOM.nextLong();
		return convid;
	}

	/**
	 *  Step to handle a stream.
	 */
//	public class HandleStreamStep	implements IComponentStep<Void>
//	{
//		private final IConnection con;
//
//		public HandleStreamStep(IConnection con)
//		{
//			this.con = con;
//		}
//
//		public IFuture<Void> execute(IInternalAccess ia)
//		{
//			invokeHandlers(con);
//			return IFuture.DONE;
//		}
//
//		/**
//		 *  Extracted to allow overriding behaviour.
//		 *  @return true, when at least one matching handler was found.
//		 */
//		protected boolean invokeHandlers(IConnection con)
//		{
//			boolean	ret	= false;
//			// Todo: Stream handlers?
////			if(messagehandlers!=null)
////			{
////				for(int i=0; i<messagehandlers.size(); i++)
////				{
////					IMessageHandler mh = (IMessageHandler)messagehandlers.get(i);
////					if(mh.getFilter().filter(message))
////					{
////						ret	= true;
////						mh.handleMessage(message.getParameterMap(), message.getMessageType());
////						if(mh.isRemove())
////						{
////							messagehandlers.remove(i);
////						}
////					}
////				}
////			}
//			return ret;
//		}
//
//		public String toString()
//		{
//			return "messageArrived()_#"+this.hashCode();
//		}
//	}
	
	/**
	 *  Message wrapper for messages awaiting a reply.
	 *
	 */
	protected static class WaitingMessageWrapper
	{
		/** The user message object */
		protected Object usermessage;
		
		/** The conversation ID */
		protected String convid;
		
		/** Flag if message is a reply. */
		protected boolean reply;
		
		/**
		 *  Creates the WaitingMessage. (Bean)
		 */
		public WaitingMessageWrapper()
		{
		}
		
		/**
		 *  Creates the WaitingMessage.
		 *  
		 *  @param usermessage The user message.
		 *  @param convid The conversation ID.
		 */
		public WaitingMessageWrapper(String convid, Object usermessage)
		{
			this.convid = convid;
			this.usermessage = usermessage;
		}
		
		/**
		 *  Gets conversation ID.
		 *  
		 *  @return conversation ID.
		 */
		public String getConversationId()
		{
			return convid;
		}
		
		/**
		 *  Sets conversation ID.
		 *  
		 *  @param convid conversation ID.
		 */
		public void setConversationId(String convid)
		{
			this.convid = convid;
		}
		
		/**
		 *  Gets the user message.
		 *  
		 *  @return The user message.
		 */
		public Object getUserMessage()
		{
			return usermessage;
		}
		
		/**
		 *  Sets the user message.
		 *  
		 *  @param usermessage The user message.
		 */
		public void setUserMessage(Object usermessage)
		{
			this.usermessage = usermessage;
		}
		
		/**
		 *  Checks if the message is a reply.
		 *  
		 *  @return True, if message is a reply.
		 */
		public boolean isReply()
		{
			return reply;
		}
		
		/**
		 *  Sets if the message is a reply.
		 *  
		 *  @param reply Set true, if message is a reply.
		 */
		public void setReply(boolean reply)
		{
			this.reply = reply;
		}
	}
}
