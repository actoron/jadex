package jadex.bridge.component.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConnection;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IMessageAdapter;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.IMessageHandler;
import jadex.bridge.component.MessageConversationFilter;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.MessageType;
import jadex.bridge.service.types.message.MessageType.ParameterSpecification;
import jadex.commons.ComposedFilter;
import jadex.commons.IFilter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Feature to send messages and receive messages via handlers.
 *  Also implements reacting to incoming stream connections (only exposed in micro agents for now).
 */
public class MessageComponentFeature extends AbstractComponentFeature implements IMessageFeature, IInternalMessageFeature
{
	//-------- attributes --------
	
	/** The list of message handlers. */
	protected List<IMessageHandler> messagehandlers;
	
	//-------- constructors --------
	
	/**
	 *  Create the feature.
	 */
	public MessageComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	//-------- IMessageFeature interface --------
	
	/**
	 *  Send a message.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	public IFuture<Void> sendMessage(Map<String, Object> me, MessageType mt)
	{
		return sendMessage(me, mt, null);
	}
	
	/**
	 *  Send a message.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	public IFuture<Void> sendMessage(final Map<String, Object> me, final MessageType mt, 
		final byte[] codecids)
	{
		final Future<Void> ret = new Future<Void>();
		
		try
		{
			IMessageService ms = SServiceProvider.getLocalService(getComponent(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM);
	//		System.err.println("send msg1: "+getComponentIdentifier()+" "+me.get(SFipa.CONTENT));
			IFuture<Void> res = ms.sendMessage(me, mt, getComponent().getComponentIdentifier(),
				getComponent().getModel().getResourceIdentifier(), null, null, codecids);
			res.addResultListener(getComponent().getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)));
	//		res.addResultListener(new IResultListener<Void>()
	//		{
	//			public void resultAvailable(Void result)
	//			{
	//				System.out.println("ok send: "+me.get(SFipa.RECEIVERS));
	//			}
	//			public void exceptionOccurred(Exception exception)
	//			{
	//				System.out.println("ex: "+exception);
	//			}
	//		});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
		return ret;
	}
	
	/**
	 *  Send a message and wait for a reply.
	 *  @param me	The message content (name value pairs).
	 *  @param mt	The message type describing the content.
	 */
	// Todo: supply reply message as future return value?
	public IFuture<Void> sendMessageAndWait(final Map<String, Object> me, final MessageType mt, final IMessageHandler handler)
	{
		boolean hasconvid = false;
		ParameterSpecification[] ps = mt.getConversationIdentifiers();
		for(int i=0; i<ps.length && !hasconvid; i++)
		{
			if(me.get(ps[i].getName())!=null)
				hasconvid = true;
		}
		if(!hasconvid)
			throw new RuntimeException("Message has no conversation identifier set: "+me);
		
		addMessageHandler(new IMessageHandler()
		{
			IFilter<IMessageAdapter> filter = handler.getFilter()==null ? new MessageConversationFilter(me, mt)
				: new ComposedFilter<IMessageAdapter>(new MessageConversationFilter(me, mt), handler.getFilter());
				
			public long getTimeout()
			{
				return handler.getTimeout();
			}	
				
			public boolean isRemove()
			{
				return handler.isRemove();
			}
			
			public void handleMessage(Map<String, Object> msg, MessageType type)
			{
				handler.handleMessage(msg, type);
			}
			
			public void timeoutOccurred()
			{
				handler.timeoutOccurred();
			}
			
			public boolean isRealtime()
			{
				return handler.isRealtime();
			}
			
			public IFilter<IMessageAdapter> getFilter()
			{
				return filter;
			}
		});
		return sendMessage(me, mt);
	}
	
	/**
	 *  Add a message handler.
	 *  @param  The handler.
	 */
	public IFuture<Void> addMessageHandler(final IMessageHandler handler)
	{
		if(handler.getFilter()==null)
			throw new RuntimeException("Filter must not be null in handler: "+handler);
			
		if(messagehandlers==null)
		{
			messagehandlers = new ArrayList<IMessageHandler>();
		}
		if(handler.getTimeout()>0)
		{
			getComponent().getComponentFeature(IExecutionFeature.class).waitForDelay(handler.getTimeout(), new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					// Only call timeout when handler is still present
					if(messagehandlers.contains(handler))
					{
						handler.timeoutOccurred();
						if(handler.isRemove())
						{
							removeMessageHandler(handler);
						}
					}
					return IFuture.DONE;
				}
			}, handler.isRealtime());
		}
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
	 *  @param message The message that arrived.
	 */
	public void messageArrived(IMessageAdapter message)
	{
		getComponent().getComponentFeature(IExecutionFeature.class)
			.scheduleStep(createHandleMessageStep(message))
			.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				// NOP
			}
			
			public void exceptionOccurred(Exception exception)
			{
				if(!(exception instanceof ComponentTerminatedException)
					|| !((ComponentTerminatedException)exception).getComponentIdentifier().equals(component.getComponentIdentifier()))
				{
					// Todo: fail fast components?
					StringWriter	sw	= new StringWriter();
					exception.printStackTrace(new PrintWriter(sw));
					getComponent().getLogger().severe("Exception during message processing\n"+sw);
				}
			}
		});
	}
	
	/**
	 *  Helper method to override stream handling.
	 *  May be called from external threads.
	 */
	protected IComponentStep<Void>	createHandleStreamStep(IConnection con)
	{
		return new HandleStreamStep(con);
	}

	/**
	 *  Inform the component that a stream has arrived.
	 *  @param con The stream that arrived.
	 */
	public void streamArrived(IConnection con)
	{
		getComponent().getComponentFeature(IExecutionFeature.class)
			.scheduleStep(createHandleStreamStep(con))
			.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				// NOP
			}
			
			public void exceptionOccurred(Exception exception)
			{
				// Todo: fail fast components?
				StringWriter	sw	= new StringWriter();
				exception.printStackTrace(new PrintWriter(sw));
				getComponent().getLogger().severe("Exception during stream processing\n"+sw);
			}
		});
	}
	
	/**
	 *  Helper method to override message handling.
	 *  May be called from external threads.
	 */
	protected IComponentStep<Void>	createHandleMessageStep(IMessageAdapter message)
	{
		return new HandleMessageStep(message);
	}
	
	/**
	 *  Step to handle a message.
	 */
	public class HandleMessageStep	implements IComponentStep<Void>
	{
		private final IMessageAdapter message;

		public HandleMessageStep(IMessageAdapter message)
		{
			this.message = message;
		}

		public IFuture<Void> execute(IInternalAccess ia)
		{
			invokeHandlers(message);
			return IFuture.DONE;
		}

		/**
		 *  Extracted to allow overriding behaviour.
		 *  @return true, when at least one matching handler was found.
		 */
		protected boolean invokeHandlers(IMessageAdapter message)
		{
			boolean	ret	= false;
			if(messagehandlers!=null)
			{
				for(int i=0; i<messagehandlers.size(); i++)
				{
					IMessageHandler mh = (IMessageHandler)messagehandlers.get(i);
					if(mh.getFilter().filter(message))
					{
						ret	= true;
						mh.handleMessage(message.getParameterMap(), message.getMessageType());
						if(mh.isRemove())
						{
							messagehandlers.remove(i);
						}
					}
				}
			}
			return ret;
		}

		public String toString()
		{
			return "messageArrived()_#"+this.hashCode();
		}
	}

	/**
	 *  Step to handle a stream.
	 */
	public class HandleStreamStep	implements IComponentStep<Void>
	{
		private final IConnection con;

		public HandleStreamStep(IConnection con)
		{
			this.con = con;
		}

		public IFuture<Void> execute(IInternalAccess ia)
		{
			invokeHandlers(con);
			return IFuture.DONE;
		}

		/**
		 *  Extracted to allow overriding behaviour.
		 *  @return true, when at least one matching handler was found.
		 */
		protected boolean invokeHandlers(IConnection con)
		{
			boolean	ret	= false;
			// Todo: Stream handlers?
//			if(messagehandlers!=null)
//			{
//				for(int i=0; i<messagehandlers.size(); i++)
//				{
//					IMessageHandler mh = (IMessageHandler)messagehandlers.get(i);
//					if(mh.getFilter().filter(message))
//					{
//						ret	= true;
//						mh.handleMessage(message.getParameterMap(), message.getMessageType());
//						if(mh.isRemove())
//						{
//							messagehandlers.remove(i);
//						}
//					}
//				}
//			}
			return ret;
		}

		public String toString()
		{
			return "messageArrived()_#"+this.hashCode();
		}
	}
}
