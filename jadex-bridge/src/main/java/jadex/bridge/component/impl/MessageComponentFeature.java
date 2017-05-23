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
import jadex.commons.collection.LRU;
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
	
	/**
	 *  Messages awaiting reply.
	 */
	protected Map<String, Future<Void>> awaitingmessages;
	
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
			byte[] body = serialserv.encode(receiver, component.getClassLoader(), message);
			getSecurityService().encryptAndSign(receiver, body).addResultListener(new ExceptionDelegationResultListener<byte[], Void>(ret)
			{
				public void customResultAvailable(final byte[] body) throws Exception
				{
					IComponentIdentifier rplat = receiver.getRoot();
					Tuple2<ITransportService, Integer> tup = getPlatformStateService().getTransportCache().get(rplat);
					if (tup != null)
					{
						getPlatformStateService().getTransportCache().put(rplat, tup);
						tup.getFirstEntity().sendMessage(header, body).addResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								ret.setResult(null);
							};
							
							public void exceptionOccurred(Exception exception)
							{
								selectTransportService(header, body).addResultListener(new IResultListener<ITransportService>()
								{
									public void resultAvailable(ITransportService result)
									{
										// Adding to cache done by select function.
										result.sendMessage(header, body).addResultListener(new DelegationResultListener<Void>(ret));
									}
									
									public void exceptionOccurred(Exception exception)
									{
										ret.setException(exception);
									}
								});
							};
						});
					}
				}
			});
		}
		
		return ret;
	}
	
	/**
	 *  Send a message and wait for a reply.
	 *  @param receiver	The message receiver.
	 *  @param message	The message.
	 *  
	 */
	public IFuture<Void> sendMessageAndWait(IComponentIdentifier receiver, Object message)
	{
		final Future<Void> ret = new Future<Void>();
		final String convid = SUtil.createUniqueId(component.getComponentIdentifier().toString());
		WaitingMessage wmsg = new WaitingMessage(convid, message);
		if (awaitingmessages == null)
			awaitingmessages = new HashMap<String, Future<Void>>();
		awaitingmessages.put(convid, ret);
		sendMessage(receiver, wmsg).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				// NOP
			}
			
			public void exceptionOccurred(Exception exception)
			{
				Future<Void> fut = awaitingmessages.remove(convid);
				if (fut != null)
					fut.setException(exception);
			}
		});
		component.getComponentFeature0(IExecutionFeature.class).waitForDelay(PlatformConfiguration.getLocalDefaultTimeout(platformid), new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				Future<Void> fut = awaitingmessages.remove(convid);
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
		if (body instanceof WaitingMessage)
		{
			final WaitingMessage wm = (WaitingMessage) body;
			WaitingMessageAnswer answer = new WaitingMessageAnswer(((WaitingMessage) body).getConversationId());
			sendMessage((IComponentIdentifier) header.get(SENDER), answer).addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					handleMessage(secinfos, header, wm.getUserMessage());
				}
				
				public void exceptionOccurred(Exception exception)
				{
				}
			});
		}
		else if (body instanceof WaitingMessageAnswer)
		{
			WaitingMessageAnswer answer = (WaitingMessageAnswer) body;
			Future<Void> fut = awaitingmessages.remove(answer.getConversationId());
			if (fut != null)
				fut.setResult(null);
		}
		else
		{
			handleMessage(secinfos, header, body);
		}
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
	
	protected IFuture<ITransportService> selectTransportService(Map<String, Object> header, byte[] body)
	{
		final Future<ITransportService> ret = new Future<ITransportService>();
		Collection<ITransportService> coll = SServiceProvider.getLocalServices(component, ITransportService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		if (coll != null && coll.size() > 0)
		{
			final IComponentIdentifier receiverplatform = ((IComponentIdentifier) header.get(RECEIVER)).getRoot();
			for (Iterator<ITransportService> it = coll.iterator(); it.hasNext(); )
			{
				final ITransportService tp = it.next();
				tp.isReady(header).addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						ret.setResultIfUndone(tp);
						tp.getPriority().addResultListener(new IResultListener<Integer>()
						{
							public void resultAvailable(Integer result)
							{
								Tuple2<ITransportService, Integer> tup = getPlatformStateService().getTransportCache().get(receiverplatform);
								if (tup == null || tup.getSecondEntity() < result)
									getPlatformStateService().getTransportCache().put(receiverplatform, new Tuple2<ITransportService, Integer>(tp, result));
							}
							
							public void exceptionOccurred(Exception exception)
							{
							}
						});
					}
					
					public void exceptionOccurred(Exception exception)
					{
					}
				});
			}
		}
		else
			ret.setException(new ServiceNotFoundException("No transport available."));
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
	 *  Reply message for messages awaiting a reply.
	 *
	 */
	protected static class WaitingMessageAnswer
	{
		/** The conversation ID */
		protected String convid;
		
		/**
		 *  Creates the WaitingMessageAnswer. (Bean)
		 *  
		 */
		public WaitingMessageAnswer()
		{
		}
		
		/**
		 *  Creates the WaitingMessageAnswer.
		 *  
		 *  @param convid The conversation ID.
		 */
		public WaitingMessageAnswer(String convid)
		{
			this.convid = convid;
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
	}
	
	/**
	 *  Message wrapper for messages awaiting a reply.
	 *
	 */
	protected static class WaitingMessage extends WaitingMessageAnswer
	{
		/** The user message object */
		protected Object usermessage;
		
		/**
		 *  Creates the WaitingMessage. (Bean)
		 */
		public WaitingMessage()
		{
		}
		
		/**
		 *  Creates the WaitingMessage.
		 *  
		 *  @param usermessage The user message.
		 *  @param convid The conversation ID.
		 */
		public WaitingMessage(String convid, Object usermessage)
		{
			super(convid);
			this.usermessage = usermessage;
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
	}
}
