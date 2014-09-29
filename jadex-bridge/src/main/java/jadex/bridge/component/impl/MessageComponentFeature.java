package jadex.bridge.component.impl;

import jadex.bridge.IComponentStep;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Feature to send messages and receive messages via handlers.
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
		
		IMessageService ms = SServiceProvider.getLocalService(getComponent(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM);
//		System.err.println("send msg1: "+getComponentIdentifier()+" "+me.get(SFipa.CONTENT));
		ms.sendMessage(me, mt, getComponent().getComponentIdentifier(),
			getComponent().getModel().getResourceIdentifier(), null, codecids)
			.addResultListener(getComponent().getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)));
		
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
	public void addMessageHandler(final IMessageHandler handler)
	{
		if(handler.getFilter()==null)
			throw new RuntimeException("Filter must not null in handler: "+handler);
			
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
	
	//-------- IInternalMessageFeature interface --------
	
	/**
	 *  Inform the component that a message has arrived.
	 *  @param message The message that arrived.
	 */
	public void messageArrived(IMessageAdapter message)
	{
		getComponent().getComponentFeature(IExecutionFeature.class)
			.scheduleStep(new HandleMessageStep(message));
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
			if(messagehandlers!=null)
			{
				for(int i=0; i<messagehandlers.size(); i++)
				{
					IMessageHandler mh = (IMessageHandler)messagehandlers.get(i);
					if(mh.getFilter().filter(message))
					{
						mh.handleMessage(message.getParameterMap(), message.getMessageType());
						if(mh.isRemove())
						{
							messagehandlers.remove(i);
						}
					}
				}
			}
			return IFuture.DONE;
		}

		public String toString()
		{
			return "messageArrived()_#"+this.hashCode();
		}
	}
}
