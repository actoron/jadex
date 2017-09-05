package jadex.bridge.component.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.management.ServiceNotFoundException;

import jadex.base.IStarterConfiguration;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IInputConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IOutputConnection;
import jadex.bridge.SFuture;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMessageHandler;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.streams.AbstractConnectionHandler;
import jadex.bridge.component.streams.AckInfo;
import jadex.bridge.component.streams.InitInfo;
import jadex.bridge.component.streams.InputConnection;
import jadex.bridge.component.streams.InputConnectionHandler;
import jadex.bridge.component.streams.OutputConnection;
import jadex.bridge.component.streams.OutputConnectionHandler;
import jadex.bridge.component.streams.StreamPacket;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.bridge.service.types.security.IMsgSecurityInfos;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.commons.future.TerminationCommand;
import jadex.commons.transformation.annotations.Classname;
import jadex.commons.transformation.traverser.SCloner;

/**
 *  Feature to send messages and receive messages via handlers.
 *  Also implements reacting to incoming stream connections (only exposed in micro agents for now).
 */
public class MessageComponentFeature extends AbstractComponentFeature implements IMessageFeature, IInternalMessageFeature
{
	//-------- constants ---------

	/** Header marker for send-reply messages. */
	public static final String SENDREPLY = "__sendreply__";
	
	/** Exception header property for error messages. */
	public static final String	EXCEPTION	= "__exception__";
	
	//-------- attributes --------
	
	/** The platform ID. */
	protected IComponentIdentifier platformid;
	
	/** The list of message handlers. */
	protected Set<IMessageHandler> messagehandlers;
	
	/** The security service. */
	protected ISecurityService secservice;
	
	/** Flag whether to allow receiving untrusted messages. */
	protected boolean allowuntrusted;
	
	/** Messages awaiting reply. */
	protected Map<String, Future<Object>> awaitingmessages;
	
	//-------- stream attributes --------
	
	/** The initiator connections. */
	protected Map<Integer, AbstractConnectionHandler> icons = new HashMap<Integer, AbstractConnectionHandler>();

	/** The participant connections. */
	protected Map<Integer, AbstractConnectionHandler> pcons = new HashMap<Integer, AbstractConnectionHandler>();
	
	//-------- monitoring attributes --------
	
	/** The current subscriptions. */
	protected Set<SubscriptionIntermediateFuture<MessageEvent>>	subscriptions;
	
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
	
	/**
	 *  Cleanup on shutdown.
	 */
	@Override
	public IFuture<Void> shutdown()
	{
		for(SubscriptionIntermediateFuture<MessageEvent> sub: SUtil.safeSet(subscriptions))
		{
			sub.setFinished();
		}
		
		return IFuture.DONE;
	}
	
	//-------- IMessageFeature interface --------
	
	/**
	 *  Send a message.
	 *  @param receiver	The message receiver.
	 *  @param message	The message.
	 *  
	 */
	public IFuture<Void> sendMessage(IComponentIdentifier receiver, Object message)
	{
		return sendMessage(receiver, message, null);
	}
	
	/**
	 *  Send a message.
	 *  @param receiver	The message receiver.
	 *  @param message	The message.
	 *  @param addheaderfields Additional header fields.
	 *  
	 */
	public IFuture<Void> sendMessage(final IComponentIdentifier receiver, Object message, Map<String, Object> addheaderfields)
	{
		final MsgHeader header = new MsgHeader();
		if (addheaderfields != null)
			for (Map.Entry<String, Object> entry : addheaderfields.entrySet())
				header.addProperty(entry.getKey(), entry.getValue());
		header.addProperty(IMsgHeader.SENDER, component.getComponentIdentifier());
		header.addProperty(IMsgHeader.RECEIVER, receiver);
		
		return sendMessage(header, message);
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
		if (awaitingmessages == null)
			awaitingmessages = new HashMap<String, Future<Object>>();
		awaitingmessages.put(convid, ret);
		
		final MsgHeader header = new MsgHeader();
		header.addProperty(IMsgHeader.SENDER, component.getComponentIdentifier());
		header.addProperty(IMsgHeader.RECEIVER, receiver);
		header.addProperty(IMsgHeader.CONVERSATION_ID, convid);
		header.addProperty(SENDREPLY, Boolean.TRUE);
		
		sendMessage(header, message).addResultListener(new IResultListener<Void>()
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
	public IFuture<Void> sendReply(IMsgHeader msgheader, Object message)
	{
//		if (!(receivedmessageid instanceof Map))
//			return new Future<Void>(new IllegalArgumentException("Cannot reply, illegal message ID or null."));
		
		IComponentIdentifier rplyrec = (IComponentIdentifier) msgheader.getProperty(IMsgHeader.SENDER);
		String convid = (String) msgheader.getProperty(IMsgHeader.CONVERSATION_ID);
		if (rplyrec == null)
			return new Future<Void>(new IllegalArgumentException("Cannot reply, reply receiver ID not found."));
		if (convid == null)
			return new Future<Void>(new IllegalArgumentException("Cannot reply, conversation ID not found."));
		
		MsgHeader header = new MsgHeader();
		header.addProperty(IMsgHeader.RECEIVER, rplyrec);
		header.addProperty(IMsgHeader.SENDER, component.getComponentIdentifier());
		header.addProperty(IMsgHeader.CONVERSATION_ID, convid);
		header.addProperty(SENDREPLY, Boolean.TRUE);
		
		return sendMessage(header, message);
	}
	
	/**
	 *  Forwards the prepared message to the transport layer.
	 *  
	 *  @param header The message header.
	 *  @param encryptedbody The encrypted message body.
	 *  @return Null, when done, exception if failed.
	 */
	public IFuture<Void> sendToTransports(final IMsgHeader header, final byte[] encryptedbody)
	{
		final Future<Void> ret = new Future<Void>();
		// Transport service is platform-level shared / no required proxy: manual decoupling
		getTransportService(header).addResultListener(component.getComponentFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<ITransportService, Void>(ret)
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
						IComponentIdentifier rplat = ((IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER)).getRoot();
						getTransportCache(platformid).remove(rplat);
						
						getTransportService(header).addResultListener(component.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<ITransportService>()
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
						}));
					};
				});
			}
		}));
		
		return ret;
	}
	
	/**
	 *  Add a message handler.
	 *  @param  The handler.
	 */
	public void addMessageHandler(final IMessageHandler handler)
	{
		if(messagehandlers==null)
		{
			messagehandlers	= new LinkedHashSet<IMessageHandler>();
		}
		messagehandlers.add(handler);
	}
	
	/**
	 *  Remove a message handler.
	 *  @param handler The handler.
	 */
	public void removeMessageHandler(IMessageHandler handler)
	{
		if(messagehandlers!=null)
		{
			messagehandlers.remove(handler);
		}
	}
	
	/**
	 *  Sets whether to allow untrusted messages.
	 *  Handlers must perform appropriate checks if set to true.
	 *  
	 *  @param allowuntrusted Set to true to allow untrusted messages.
	 */
	public void setAllowUntrusted(boolean allowuntrusted)
	{
		this.allowuntrusted = allowuntrusted;
	}
	
	//-------- IInternalMessageFeature interface --------
	
	/**
	 *  Inform the component that a message has arrived.
	 *  Called from transports (i.e. remote messages).
	 *  
	 *  @param header The message header.
	 *  @param bodydata The encrypted message that arrived.
	 */
	public void messageArrived(final IMsgHeader header, byte[] bodydata)
	{
		if(header != null && bodydata != null)
		{
			getSecurityService().decryptAndAuth((IComponentIdentifier) header.getProperty(IMsgHeader.SENDER), bodydata).addResultListener(new IResultListener<Tuple2<IMsgSecurityInfos,byte[]>>()
			{
				public void resultAvailable(Tuple2<IMsgSecurityInfos, byte[]> result)
				{
					// Check if SecurityService ok'd it at all.
					if(result != null)
					{
						final IMsgSecurityInfos secinf = result.getFirstEntity();
						
						// Only accept messages we trust.
						if(secinf.isAuthenticated() || allowuntrusted)
						{
							Object message;
							try
							{
								message = deserializeMessage(header, result.getSecondEntity());
							}
							catch(Exception e)
							{
								// When decoding message fails -> allow agent to handle exception (e.g. useful for failed replies)
								message = null;
								header.addProperty(EXCEPTION, e);
							}
							messageArrived(secinf, header, message);
						}
					}
				};
				
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
				}
			});
		}
		else
		{
			getComponent().getLogger().warning("Received empty message: "+header+" "+bodydata);
		}
	}
	
	/**
	 *  Inform the component that a message has arrived.
	 *  Called directly for intra-platform message delivery (i.e. local messages)
	 *  and indirectly for remote messages.
	 *  
	 *  @param secinfos The security meta infos.
	 *  @param header The message header.
	 *  @param body The message that arrived.
	 */
	public void messageArrived(final IMsgSecurityInfos secinfos, final IMsgHeader header, Object body)
	{
		notifyMessageReceived(secinfos, header, body);
		
		if(Boolean.TRUE.equals(header.getProperty(SENDREPLY)))
		{
			// send-reply message, check if reply.
			String convid = (String) header.getProperty(IMsgHeader.CONVERSATION_ID);
			Future<Object> fut = awaitingmessages != null ? awaitingmessages.remove(convid) : null;
			if(fut != null)
			{
				Exception exception = (Exception) header.getProperty(EXCEPTION);
				if(exception != null)
					fut.setException(exception);
				else
					fut.setResult(body);
				return;
			}
			else
			{
				handleMessage(secinfos, header, body);
			}
		}
		else if(body instanceof StreamPacket)
		{
			StreamPacket packet = (StreamPacket)body;
			byte type = packet.getType();
			int conid = packet.getConnectionId().intValue();
			
			System.out.println("rec stream msg: "+getComponent().getComponentIdentifier().getLocalName()+" "+type);
			
			// Handle output connection participant side
			if(type==AbstractConnectionHandler.INIT_OUTPUT_INITIATOR)
			{
				InitInfo ii = (InitInfo)packet.getData();
				initInputConnection(conid, ii.getInitiator(), ii.getParticipant(), ii.getNonFunctionalProperties());
			}
			else if(type==AbstractConnectionHandler.ACKINIT_OUTPUT_PARTICIPANT)
			{
//				System.out.println("CCC: ack init");
				OutputConnectionHandler och = (OutputConnectionHandler)icons.get(Integer.valueOf(conid));
				if(och!=null)
				{
					och.ackReceived(AbstractConnectionHandler.INIT, packet.getData());
				}
				else
				{
					System.out.println("OutputStream not found (ackinit): "+component+", "+System.currentTimeMillis()+", "+conid);
				}
			}
			else if(type==AbstractConnectionHandler.DATA_OUTPUT_INITIATOR)
			{
//				System.out.println("received data");
				InputConnectionHandler ich = (InputConnectionHandler)pcons.get(Integer.valueOf(conid));
				if(ich!=null)
				{
					ich.addData(packet.getSequenceNumber(), (byte[])packet.getData());
				}
				else
				{
					System.out.println("InputStream not found (dai): "+conid+" "+pcons+" "+getComponent().getComponentIdentifier());
				}
			}
			else if(type==AbstractConnectionHandler.CLOSE_OUTPUT_INITIATOR)
			{
//				System.out.println("CCC: close");
				InputConnectionHandler ich = (InputConnectionHandler)pcons.get(Integer.valueOf(conid));
				if(ich!=null)
				{
					ich.closeReceived(SUtil.bytesToInt((byte[])packet.getData()));
				}
				else
				{
					System.out.println("InputStream not found (coi): "+component+", "+System.currentTimeMillis()+", "+conid);
				}
			}
			else if(type==AbstractConnectionHandler.ACKCLOSE_OUTPUT_PARTICIPANT)
			{
//				System.out.println("CCC: ackclose");
				OutputConnectionHandler och = (OutputConnectionHandler)icons.get(Integer.valueOf(conid));
				if(och!=null)
				{
					och.ackReceived(AbstractConnectionHandler.CLOSE, packet.getData());
				}
				else
				{
					System.out.println("OutputStream not found (ackclose): "+component+", "+System.currentTimeMillis()+", "+conid);
				}
			}
			else if(type==AbstractConnectionHandler.CLOSEREQ_OUTPUT_PARTICIPANT)
			{
//				System.out.println("CCC: closereq");
				OutputConnectionHandler och = (OutputConnectionHandler)icons.get(Integer.valueOf(conid));
				if(och!=null)
				{
					och.closeRequestReceived();
				}
				else
				{
					System.out.println("OutputStream not found (closereq): "+component+", "+System.currentTimeMillis()+", "+conid);
				}
			}
			else if(type==AbstractConnectionHandler.ACKCLOSEREQ_OUTPUT_INITIATOR)
			{
//				System.out.println("CCC: ackclosereq");
				InputConnectionHandler ich = (InputConnectionHandler)pcons.get(Integer.valueOf(conid));
				if(ich!=null)
				{
					ich.ackReceived(AbstractConnectionHandler.CLOSEREQ, packet.getData());
//					ich.ackCloseRequestReceived();
				}
				else
				{
					System.out.println("OutputStream not found (ackclosereq): "+component+", "+System.currentTimeMillis()+", "+conid);
				}
			}
			else if(type==AbstractConnectionHandler.ACKDATA_OUTPUT_PARTICIPANT)
			{
				// Handle input connection initiator side
				OutputConnectionHandler och = (OutputConnectionHandler)icons.get(Integer.valueOf(conid));
				if(och!=null)
				{
					AckInfo ackinfo = (AckInfo)packet.getData();
					och.ackDataReceived(ackinfo);
				}
				else
				{
					System.out.println("OutputStream not found (ackdata): "+component+", "+System.currentTimeMillis()+", "+conid);
				}
			}
			
			else if(type==AbstractConnectionHandler.INIT_INPUT_INITIATOR)
			{
				InitInfo ii = (InitInfo)packet.getData();
				initOutputConnection(conid, ii.getInitiator(), ii.getParticipant(), ii.getNonFunctionalProperties());
			}
			else if(type==AbstractConnectionHandler.ACKINIT_INPUT_PARTICIPANT)
			{
				InputConnectionHandler ich = (InputConnectionHandler)icons.get(Integer.valueOf(conid));
				if(ich!=null)
				{
					ich.ackReceived(AbstractConnectionHandler.INIT, packet.getData());
				}
				else
				{
					System.out.println("InputStream not found (ackinit): "+component+", "+System.currentTimeMillis()+", "+conid);
				}
			}
			else if(type==AbstractConnectionHandler.DATA_INPUT_PARTICIPANT)
			{
				InputConnectionHandler ich = (InputConnectionHandler)icons.get(Integer.valueOf(conid));
				if(ich!=null)
				{
					ich.addData(packet.getSequenceNumber(), (byte[])packet.getData());
				}
				else
				{
					System.out.println("InputStream not found (data input): "+conid);
				}
			}
			else if(type==AbstractConnectionHandler.ACKDATA_INPUT_INITIATOR)
			{
				OutputConnectionHandler och = (OutputConnectionHandler)pcons.get(Integer.valueOf(conid));
				if(och!=null)
				{
					AckInfo ackinfo = (AckInfo)packet.getData();
					och.ackDataReceived(ackinfo);	
				}
				else
				{
					System.out.println("OutputStream not found (ackdata): "+component+", "+System.currentTimeMillis()+", "+conid);
				}
			}
			else if(type==AbstractConnectionHandler.CLOSEREQ_INPUT_INITIATOR)
			{
				OutputConnectionHandler och = (OutputConnectionHandler)pcons.get(Integer.valueOf(conid));
				if(och!=null)
				{
					och.closeRequestReceived();
				}
				else
				{
					System.out.println("InputStream not found (closereq): "+conid);
				}
			}
			else if(type==AbstractConnectionHandler.ACKCLOSEREQ_INPUT_PARTICIPANT)
			{
				InputConnectionHandler ich = (InputConnectionHandler)icons.get(Integer.valueOf(conid));
				if(ich!=null)
				{
					ich.ackReceived(AbstractConnectionHandler.CLOSEREQ, packet.getData());
				}
				else
				{
					System.out.println("InputStream not found (ackclosereq): "+component+", "+System.currentTimeMillis()+", "+conid);
				}
			}
			else if(type==AbstractConnectionHandler.CLOSE_INPUT_PARTICIPANT)
			{
				InputConnectionHandler ich = (InputConnectionHandler)icons.get(Integer.valueOf(conid));
				if(ich!=null)
				{
					ich.closeReceived(SUtil.bytesToInt((byte[])packet.getData()));
				}
				else
				{
					System.out.println("OutputStream not found (closeinput): "+component+", "+System.currentTimeMillis()+", "+conid);
				}
			}
			else if(type==AbstractConnectionHandler.ACKCLOSE_INPUT_INITIATOR)
			{
				OutputConnectionHandler ich = (OutputConnectionHandler)pcons.get(Integer.valueOf(conid));
				if(ich!=null)
				{
					ich.ackReceived(AbstractConnectionHandler.CLOSE, packet.getData());
				}
				else
				{
					System.out.println("InputStream not found (ackclose): "+component+", "+System.currentTimeMillis()+", "+conid);
				}
			}
			
			// Handle lease time update
			else if(type==AbstractConnectionHandler.ALIVE_INITIATOR)
			{
//				System.out.println("alive initiator");
				AbstractConnectionHandler con = (AbstractConnectionHandler)pcons.get(Integer.valueOf(conid));
				if(con!=null)
				{
					con.setAliveTime(System.currentTimeMillis());
				}
				else
				{
					System.out.println("Stream not found (alive ini): "+component+", "+System.currentTimeMillis()+", "+conid);
				}
			}
			else if(type==AbstractConnectionHandler.ALIVE_PARTICIPANT)
			{
//				System.out.println("alive particpant");
				AbstractConnectionHandler con = (AbstractConnectionHandler)icons.get(Integer.valueOf(conid));
				if(con!=null)
				{
					con.setAliveTime(System.currentTimeMillis());
				}
				else
				{
					System.out.println("Stream not found (alive par): "+component+", "+System.currentTimeMillis()+", "+conid);
				}
			}
//	
////		System.out.println("bbbb: "+mycnt+" "+getComponent().getComponentIdentifier());
//			}
////					catch(Throwable e)
//					catch(final Exception e)
//					{
////						e.printStackTrace();
//						getComponent().scheduleStep(new IComponentStep<Void>()
//						{
//							public IFuture<Void> execute(IInternalAccess ia)
//							{
//								ia.getLogger().warning("Exception in stream: "+e.getMessage());
//								return IFuture.DONE;
//							}
//						});
//					}
//				}
//			}
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
	 *  @param body
	 */
	protected void handleMessage(final IMsgSecurityInfos secinf, final IMsgHeader header, final Object body)
	{
		boolean	handled	= false;
		if(messagehandlers!=null)
		{
			for(Iterator<IMessageHandler> it = messagehandlers.iterator(); it.hasNext(); )
			{
				final IMessageHandler handler = it.next();
				if(handler.isRemove())
				{
					it.remove();
				}
				else if(handler.isHandling(secinf, header, body))
				{
					handled	= true;
//					component.getComponentFeature0(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
//					{
//						public IFuture<Void> execute(IInternalAccess ia)
//						{
							handler.handleMessage(secinf, header, body);
//							return IFuture.DONE;
//						}
//					});
				}
			}
		}
		
		if(!handled)
			processUnhandledMessage(secinf, header, body);
	}
	
	/**
	 *  Send the message to potentially multiple receivers.
	 *  
	 *  @param header The header.
	 *  @param message The message.
	 *  @return Null, when sent.
	 */
	protected IFuture<Void> sendMessage(final MsgHeader header, Object message)
	{
		preprocessMessage(header, message);
		
		if(subscriptions!=null && !subscriptions.isEmpty()
			&& header.getProperty(IMsgHeader.XID)==null)
		{
			header.addProperty(IMsgHeader.XID, SUtil.createUniqueId(null));
		}
		
		Object rec = header.getProperty(IMsgHeader.RECEIVER);
		
		if(SReflect.isIterable(rec))
		{
			FutureBarrier<Void>	fubar	= new FutureBarrier<Void>();
			for(Object orec: SReflect.getIterable(rec))
			{
				// Need to copy header, because receiver isn't exposed in transport interface.
				MsgHeader	copy	= new MsgHeader();
				copy.setProperties(new LinkedHashMap<String, Object>(header.getProperties()));
				copy.addProperty(IMsgHeader.RECEIVER, orec);
				fubar.addFuture(doSendMessage(copy, message));
			}
			return fubar.waitFor();
		}
		else
		{
			return doSendMessage(header, message);
		}
	}
		
	/**
	 *  Send the message to a single receiver.
	 *  
	 *  @param header The header.
	 *  @param message The message.
	 *  @return Null, when sent.
	 */
	protected IFuture<Void> doSendMessage(final MsgHeader header, Object message)
	{
		final Future<Void> ret = new Future<Void>();

		Object rec = header.getProperty(IMsgHeader.RECEIVER);		
		if(!(rec instanceof IComponentIdentifier))
		{
			return new Future<Void>(new IllegalArgumentException("Messages must have receiver(s) of type IComponentIdentifier: "+message+", "+header));
		}
		IComponentIdentifier receiver = (IComponentIdentifier)rec;
		
		notifyMessageSent(header, message);
		
		if(receiver.getRoot().equals(platformid))
		{
			// Direct local delivery.
			ClassLoader cl = SComponentManagementService.getLocalClassLoader(receiver);
			final Object clonedmsg = SCloner.clone(message, cl);
			
			SComponentManagementService.getLocalExternalAccess(receiver).scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					IMessageFeature imf = ia.getComponentFeature0(IMessageFeature.class);
					
					if (imf instanceof IInternalMessageFeature)
					{
						((IInternalMessageFeature)imf).messageArrived(null, header, clonedmsg);
						return IFuture.DONE;
					}
					
					return new Future<Void>(new RuntimeException("Receiver " + ia.getComponentIdentifier() + " has no messaging."));
				}
			}).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			try
			{
				ISerializationServices serialserv = getSerializationServices(platformid);
				byte[] body = serialserv.encode(header, component, message);
				getSecurityService().encryptAndSign(header, body).addResultListener(new ExceptionDelegationResultListener<byte[], Void>((Future<Void>) ret)
				{
					public void customResultAvailable(final byte[] body) throws Exception
					{
						sendToTransports(header, body).addResultListener(new DelegationResultListener<Void>(ret));
					}
				});
			}
			catch(Exception e)
			{
				// Encode failed -> return exception
				ret.setException(e);
			}
		}
		
		return ret;
	}

	/**
	 *  Called for all messages without matching message handlers.
	 *  Can be overwritten by specific message feature implementations (e.g. micro or BDI).
	 */
	protected void processUnhandledMessage(final IMsgSecurityInfos secinf, final IMsgHeader header, final Object body)
	{
	}
	
	/**
	 *  Deserialize the message.
	 *  
	 *  @param header The message header.
	 *  @param serializedmsg The serialized message.
	 *  @return The deserialized message.
	 */
	protected Object deserializeMessage(IMsgHeader header, byte[] serializedmsg)
	{
		return getSerializationServices(platformid).decode(header, component, serializedmsg);
	}
	
	/**
	 *  Inform the component that a stream has arrived.
	 *  @param con The stream that arrived.
	 */
	public void streamArrived(IConnection con)
	{
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
//				StringWriter sw = new StringWriter();
//				exception.printStackTrace(new PrintWriter(sw));
//				getComponent().getLogger().severe("Exception during stream processing\n"+sw);
//			}
//		});
	}
	
//	/**
//	 *  Create a new stream step.
//	 */
//	public IComponentStep<Void> createHandleStreamStep(IConnection con)
//	{
//		return new HandleStreamStep(con);
//	}
//	
//	/**
//	 *  Step to handle a stream.
//	 *  Must not do anything and just throw it away?
//	 */
//	public class HandleStreamStep implements IComponentStep<Void>
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
//			return ret;
//		}
//
//		public String toString()
//		{
//			return "messageArrived()_#"+this.hashCode();
//		}
//	}
	
	/**
	 *  Find a suitable transport service for a message.
	 *  
	 *  @param header The message header.
	 *  @return A suitable transport service or exception if none is available.
	 */
	protected IFuture<ITransportService> getTransportService(IMsgHeader header)
	{
		final Future<ITransportService> ret = new Future<ITransportService>();
		IComponentIdentifier rplat = ((IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER)).getRoot();
		
		Tuple2<ITransportService, Integer> tup = getTransportCache(platformid).get(rplat);
		if (tup != null)
		{
			getTransportCache(platformid).put(rplat, tup);
			ret.setResult(tup.getFirstEntity());
		}
		else
		{
//			final Collection<ITransportService> coll = SServiceProvider.getLocalServices(component, ITransportService.class, RequiredServiceInfo.SCOPE_PLATFORM);
			final Collection<ITransportService> coll = SServiceProvider.getLocalServices(component, ITransportService.class, RequiredServiceInfo.SCOPE_PLATFORM, false);
			if (coll != null && coll.size() > 0)
			{
				final IComponentIdentifier receiverplatform = ((IComponentIdentifier) header.getProperty(IMsgHeader.RECEIVER)).getRoot();
				final int[] counter = new int[] { coll.size() };
				for (Iterator<ITransportService> it = coll.iterator(); it.hasNext(); )
				{
					final ITransportService tp = it.next();
					tp.isReady(header).addResultListener(new IResultListener<Integer>()
					{
						public void resultAvailable(Integer priority)
						{
							ret.setResultIfUndone(tp);
							Tuple2<ITransportService, Integer> tup = getTransportCache(platformid).get(receiverplatform);
							if (tup == null || tup.getSecondEntity() < priority)
								getTransportCache(platformid).put(receiverplatform, new Tuple2<ITransportService, Integer>(tp, priority));
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
			secservice = SServiceProvider.getLocalService(component, ISecurityService.class, RequiredServiceInfo.SCOPE_PLATFORM, false);
		return secservice;
	}
	
	/**
	 *  Gets the platform serialization services.
	 *  
	 *  @param platformid The platform ID.
	 *  @return The serialization services.
	 */
	public static final ISerializationServices getSerializationServices(IComponentIdentifier platformid)
	{
		return (ISerializationServices) PlatformConfiguration.getPlatformValue(platformid.getRoot(), IStarterConfiguration.DATA_SERIALIZATIONSERVICES);
	}
	
	/**
	 *  Gets the transport cache services.
	 *  
	 *  @param platformid The platform ID.
	 *  @return The transport cache.
	 */
	@SuppressWarnings("unchecked")
	public static final Map<IComponentIdentifier, Tuple2<ITransportService, Integer>> getTransportCache(IComponentIdentifier platformid)
	{
		return (Map<IComponentIdentifier, Tuple2<ITransportService, Integer>>) PlatformConfiguration.getPlatformValue(platformid.getRoot(), IStarterConfiguration.DATA_TRANSPORTCACHE);
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
	 *  Get the participant input connection.
	 */
	public IInputConnection getParticipantInputConnection(int conid, IComponentIdentifier initiator, IComponentIdentifier participant, Map<String, Object> nonfunc)
	{
		return initInputConnection(conid, initiator, participant, nonfunc);
	}
	
	/**
	 *  Get the participant output connection.
	 */
	public IOutputConnection getParticipantOutputConnection(int conid, IComponentIdentifier initiator, IComponentIdentifier participant, Map<String, Object> nonfunc)
	{
		return initOutputConnection(conid, initiator, participant, nonfunc);
	}
	
	/**
	 *  Create a virtual output connection.
	 */
	public OutputConnection internalCreateOutputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc)
	{
		UUID uuconid = UUID.randomUUID();
		int conid = uuconid.hashCode();
		OutputConnectionHandler och = new OutputConnectionHandler(getComponent(), nonfunc);
		icons.put(conid, och);
		OutputConnection con = new OutputConnection(sender, receiver, conid, true, och);
//			System.out.println("created ocon: "+component+", "+System.currentTimeMillis()+", "+och.getConnectionId());
		return con;
	}
	
	/**
	 *  Create a virtual output connection.
	 */
	public IFuture<IOutputConnection> createOutputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc)
	{
		return new Future<IOutputConnection>(internalCreateOutputConnection(sender, receiver, nonfunc));
	}

	/**
	 *  Create a virtual input connection.
	 */
	public InputConnection internalCreateInputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc)
	{
		UUID uuconid = UUID.randomUUID();
		int conid = uuconid.hashCode();
		InputConnectionHandler ich = new InputConnectionHandler(getComponent(), nonfunc);
		icons.put(conid, ich);
		InputConnection con = new InputConnection(sender, receiver, conid, true, ich);
//			System.out.println("created icon: "+component+", "+System.currentTimeMillis()+", "+ich.getConnectionId());
		return con;
	}
	
	/**
	 *  Create a virtual input connection.
	 */
	public IFuture<IInputConnection> createInputConnection(IComponentIdentifier sender, IComponentIdentifier receiver, Map<String, Object> nonfunc)
	{
		return new Future<IInputConnection>(internalCreateInputConnection(sender, receiver, nonfunc));
	}
	
	/**
	 *  Create local input connection side after receiving a remote init output message.
	 *  May be called multiple times and does nothing, if connection already exists.
	 */
	protected IInputConnection	initInputConnection(final int conid, final IComponentIdentifier initiator, 
		final IComponentIdentifier participant, final Map<String, Object> nonfunc)
	{
		boolean	created;
		InputConnectionHandler ich	= null;
		InputConnection con	= null;
		synchronized(this)
		{
			ich	= (InputConnectionHandler)pcons.get(Integer.valueOf(conid));
			if(ich==null)
			{
				ich = new InputConnectionHandler(getComponent(), nonfunc);
				con = new InputConnection(initiator, participant, conid, false, ich);
				pcons.put(Integer.valueOf(conid), ich);
//				System.out.println("created for: "+conid+" "+pcons+" "+getComponent().getComponentIdentifier());
				created	= true;
			}
			else
			{
				con	= ich.getInputConnection();
				created	= false;
			}
		}
		
		if(created)
		{
			ich.initReceived();
			
			final InputConnection fcon = con;
//			final Future<Void> ret = new Future<Void>();
			
			streamArrived(fcon);
			
//			IInternalMessageFeature	com	= (IInternalMessageFeature)getComponent().getComponentFeature(IMessageFeature.class);
//			if(com!=null)
//			{
//				com.streamArrived(fcon);
//			}
//			else
//			{
//				getComponent().getLogger().warning("Component received stream, but ha no communication feature: "+fcon);
//			}
			
//			SServiceProvider.getService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//			{
//				public void customResultAvailable(IComponentManagementService cms)
//				{
//					cms.getExternalAccess(participant).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
//					{
//						public void customResultAvailable(IExternalAccess ea)
//						{
//							ea.scheduleStep(new IComponentStep<Void>()
//							{
//								public IFuture<Void> execute(IInternalAccess ia)
//								{
//									IInternalMessageFeature	com	= (IInternalMessageFeature)ia.getComponentFeature(IMessageFeature.class);
//									if(com!=null)
//									{
//										com.streamArrived(fcon);
//									}
//									else
//									{
//										ia.getLogger().warning("Component received stream, but ha no communication feature: "+fcon);
//									}
//									
//									return IFuture.DONE;
//								}
//							});
//						}
//					});
//				}
//			});
		}
		else
		{
			// If connection arrives late
			if(nonfunc!=null)
				ich.setNonFunctionalProperties(nonfunc);
		}
		
		return con;
	}

	/**
	 *  Create local output connection side after receiving a remote init input message.
	 *  May be called multiple times and does nothing, if connection already exists.
	 */
	protected IOutputConnection	initOutputConnection(final int conid, final IComponentIdentifier initiator, 
		final IComponentIdentifier participant, final Map<String, Object> nonfunc)
	{
		boolean	created;
		OutputConnectionHandler och;
		OutputConnection con	= null;
		synchronized(this)
		{
			och	= (OutputConnectionHandler) pcons.get(Integer.valueOf(conid));
			if(och==null)
			{
				och = new OutputConnectionHandler(getComponent(), nonfunc);
				con = new OutputConnection(initiator, participant, conid, false, och);
				pcons.put(Integer.valueOf(conid), och);
//				System.out.println("created: "+con.hashCode());
				created	= true;
			}
			else
			{
				con	= och.getOutputConnection();
				created	= false;
			}
		}
		
		if(created)
		{
			och.initReceived();
			
			final OutputConnection	fcon	= con;
//			final Future<Void> ret = new Future<Void>();
			
			streamArrived(fcon);
			
//			IInternalMessageFeature	com	= (IInternalMessageFeature)getComponent().getComponentFeature(IMessageFeature.class);
//			if(com!=null)
//			{
//				com.streamArrived(fcon);
//			}
//			else
//			{
//				getComponent().getLogger().warning("Component received stream, but ha no communication feature: "+fcon);
//			}
			
//			SServiceProvider.getService(component, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//				.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
//			{
//				public void customResultAvailable(IComponentManagementService cms)
//				{
//					cms.getExternalAccess(participant).addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
//					{
//						public void customResultAvailable(IExternalAccess ea)
//						{
//							ea.scheduleStep(new IComponentStep<Void>()
//							{
//								public IFuture<Void> execute(IInternalAccess ia)
//								{
//									IInternalMessageFeature	com	= (IInternalMessageFeature)ia.getComponentFeature(IMessageFeature.class);
//									if(com!=null)
//									{
//										com.streamArrived(fcon);
//									}
//									else
//									{
//										ia.getLogger().warning("Component received stream, but ha no communication feature: "+fcon);
//									}
//									
//									return IFuture.DONE;
//								}
//							});
//						}
//					});
//				}
//			});
		}
		else
		{
			// If connection arrives late
			if(nonfunc!=null)
				och.setNonFunctionalProperties(nonfunc);
		}
		
		return con;
	}
	
	/**
	 * 
	 */
	public void startStreamSendAliveBehavior()
	{
		final long lt = getMinLeaseTime(getComponent().getComponentIdentifier());
		if(lt!=Timeout.NONE)
		{
			getComponent().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				@Classname("sendAlive")
				public IFuture<Void> execute(IInternalAccess ia)
				{
	//				System.out.println("sendAlive: "+pcons+" "+icons);
					AbstractConnectionHandler[] mypcons = (AbstractConnectionHandler[])pcons.values().toArray(new AbstractConnectionHandler[0]);
					for(int i=0; i<mypcons.length; i++)
					{
						if(!mypcons[i].isClosed())
						{
							mypcons[i].sendAlive();
						}
					}
					AbstractConnectionHandler[] myicons = (AbstractConnectionHandler[])icons.values().toArray(new AbstractConnectionHandler[0]);
					for(int i=0; i<myicons.length; i++)
					{
						if(!myicons[i].isClosed())
						{
							myicons[i].sendAlive();
						}
					}
					
					getComponent().getComponentFeature(IExecutionFeature.class).waitForDelay(lt, this, true);
					
					return IFuture.DONE;
				}
			});
		}
	}
	
	/**
	 * 
	 */
	public void startStreamCheckAliveBehavior()
	{
		final long lt = getMinLeaseTime(getComponent().getComponentIdentifier());
//		System.out.println("to is: "+lt);
		if(lt!=Timeout.NONE)
		{
			getComponent().getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				@Classname("checkAlive")
				public IFuture<Void> execute(IInternalAccess ia)
				{
	//				final IComponentStep<Void> step = this;
	//				final Future<Void> ret = new Future<Void>();
					
					AbstractConnectionHandler[] mypcons = (AbstractConnectionHandler[])pcons.values().toArray(new AbstractConnectionHandler[0]);
					for(int i=0; i<mypcons.length; i++)
					{
						if(!mypcons[i].isConnectionAlive())
						{
	//						System.out.println("removed con: "+component+", "+System.currentTimeMillis()+", "+mypcons[i].getConnectionId());
							mypcons[i].close();
							pcons.remove(Integer.valueOf(mypcons[i].getConnectionId()));
						}
					}
					AbstractConnectionHandler[] myicons = (AbstractConnectionHandler[])icons.values().toArray(new AbstractConnectionHandler[0]);
					for(int i=0; i<myicons.length; i++)
					{
						if(!myicons[i].isConnectionAlive())
						{
	//						System.out.println("removed con: "+component+", "+System.currentTimeMillis()+", "+myicons[i].getConnectionId());
							myicons[i].close();
							icons.remove(Integer.valueOf(myicons[i].getConnectionId()));
						}
					}
					
					getComponent().getComponentFeature(IExecutionFeature.class).waitForDelay(lt, this, true);
					
					return IFuture.DONE;
				}
			});
		}
	}
	
	/**
	 *  Get the minimum lease time.
	 *  @param platform The (local) platform.
	 *  @return The minimum leasetime.
	 */
	public static long getMinLeaseTime(IComponentIdentifier platform)
	{
		return Starter.getScaledRemoteDefaultTimeout(platform, 1.0/6);
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
//	protected static class WaitingMessageWrapper implements IMessageId
//	{
//		/** The user message object */
//		protected Object usermessage;
//		
//		/** The conversation ID */
//		protected String convid;
//		
//		/** Flag if message is a reply. */
//		protected boolean reply;
//		
//		/**
//		 *  Creates the WaitingMessage. (Bean)
//		 */
//		public WaitingMessageWrapper()
//		{
//		}
//		
//		/**
//		 *  Creates the WaitingMessage.
//		 *  
//		 *  @param usermessage The user message.
//		 *  @param convid The conversation ID.
//		 */
//		public WaitingMessageWrapper(String convid, Object usermessage)
//		{
//			this.convid = convid;
//			this.usermessage = usermessage;
//		}
//		
//		/**
//		 *  Gets conversation ID.
//		 *  
//		 *  @return conversation ID.
//		 */
//		public String getConversationId()
//		{
//			return convid;
//		}
//		
//		/**
//		 *  Sets conversation ID.
//		 *  
//		 *  @param convid conversation ID.
//		 */
//		public void setConversationId(String convid)
//		{
//			this.convid = convid;
//		}
//		
//		/**
//		 *  Gets the user message.
//		 *  
//		 *  @return The user message.
//		 */
//		public Object getUserMessage()
//		{
//			return usermessage;
//		}
//		
//		/**
//		 *  Sets the user message.
//		 *  
//		 *  @param usermessage The user message.
//		 */
//		public void setUserMessage(Object usermessage)
//		{
//			this.usermessage = usermessage;
//		}
//		
//		/**
//		 *  Checks if the message is a reply.
//		 *  
//		 *  @return True, if message is a reply.
//		 */
//		public boolean isReply()
//		{
//			return reply;
//		}
//		
//		/**
//		 *  Sets if the message is a reply.
//		 *  
//		 *  @param reply Set true, if message is a reply.
//		 */
//		public void setReply(boolean reply)
//		{
//			this.reply = reply;
//		}
//	}
	
	/** Cache for message preprocessors. */
	protected static final Map<Class<?>, IMessagePreprocessor<Object>>	preprocessors	= Collections.synchronizedMap(new HashMap<Class<?>, IMessagePreprocessor<Object>>());
	
	/**
	 *  Preprocess a message before sending.
	 *  Allows adding special treatment of certain user message types
	 *  like FIPA messages.
	 *  @param header	The message header, may be changed by preprocessor.
	 *  @param msg	The user object, may be changed by preprocessor.
	 */
	protected static void	preprocessMessage(IMsgHeader header, Object msg)
	{
		IMessagePreprocessor<Object>	proc= getPreprocessor(msg);			
		if(proc!=null)
		{
			proc.preprocessMessage(header, msg);
		}
	}

	/**
	 *  Try to find a message preprocessor for the given object.
	 *  A preprocessor has the same packe and class name, appending "Preprocessor"
	 *  and implementing IMessagePreprocessor.
	 *  Also checks superclasses and interfaces.
	 */
	protected static IMessagePreprocessor<Object> getPreprocessor(Object msg)
	{
		IMessagePreprocessor<Object> proc	= null;
		if(msg!=null)
		{
			Class<?>	clazz	= msg.getClass();
			if(preprocessors.containsKey(clazz))
			{
				proc	= preprocessors.get(clazz);
			}
			else
			{
				// Try class itself
				proc	= findPreprocessor(clazz);
				
				// Try interfaces
				if(proc==null)
				{
					for(Class<?> inter: clazz.getInterfaces())
					{
						proc	= findPreprocessor(inter);
						if(proc!=null)
						{
							break;
						}
					}
				}
				
				// Try super classes
				if(proc==null)
				{
					Class<?>	sup	= clazz;
					while(proc==null && sup.getSuperclass()!=null)
					{
						sup	= sup.getSuperclass();
						proc	= findPreprocessor(sup);
					}
				}
				
				preprocessors.put(clazz, proc);
			}
		}
		return proc;
	}
	
	/**
	 *  Try to load a message preprocessor for a given class.
	 */
	protected static IMessagePreprocessor<Object>	findPreprocessor(Class<?> clazz)
	{
		IMessagePreprocessor<Object>	ret	= null;
		try
		{
			@SuppressWarnings("unchecked")
			Class<? extends IMessagePreprocessor<Object>> pclazz	= (Class<? extends IMessagePreprocessor<Object>>)
				Class.forName(clazz.getName()+"Preprocessor", true, clazz.getClassLoader());
			ret	= pclazz.newInstance();
		}
		catch(ClassNotFoundException e)
		{
			// ignore
		}
		catch(NoClassDefFoundError e)
		{
			// ignore
		}
		catch(Throwable t)
		{
			// Class found, but e.g. instantiation or class cast exception
			throw SUtil.throwUnchecked(t);
		}
		
		return ret;
	}
	
	//-------- monitoring --------
	
	/**
	 *  Called for each sent message.
	 */
	protected void	notifyMessageSent(IMsgHeader header, Object body)
	{
		if(subscriptions!=null)
		{
			MessageEvent	event	= new MessageEvent(MessageEvent.Type.SENT, null, header, body);
			for(SubscriptionIntermediateFuture<MessageEvent> sub: subscriptions)
			{
				sub.addIntermediateResult(event);
			}
		}
	}
	
	/**
	 *  Called for each received message.
	 */
	protected void	notifyMessageReceived(IMsgSecurityInfos secinfos, IMsgHeader header, Object body)
	{
		if(subscriptions!=null)
		{
			MessageEvent	event	= new MessageEvent(MessageEvent.Type.RECEIVED, secinfos, header, body);
			for(SubscriptionIntermediateFuture<MessageEvent> sub: subscriptions)
			{
				sub.addIntermediateResult(event);
			}
		}		
	}

	/**
	 *  Listen to message events (send and receive).
	 */
	// Todo: message matching?
	public ISubscriptionIntermediateFuture<MessageEvent>	getMessageEvents()
	{
		if(subscriptions==null)
		{
			subscriptions	= new LinkedHashSet<SubscriptionIntermediateFuture<MessageEvent>>();
		}
		@SuppressWarnings("unchecked")
		final SubscriptionIntermediateFuture<MessageEvent>	ret	= (SubscriptionIntermediateFuture<MessageEvent>)
			SFuture.getNoTimeoutFuture(SubscriptionIntermediateFuture.class, getComponent());
		ret.setTerminationCommand(new TerminationCommand()
		{
			@Override
			public void terminated(Exception reason)
			{
				subscriptions.remove(ret);
			}
		});
		subscriptions.add(ret);
		return ret;
	}
}
