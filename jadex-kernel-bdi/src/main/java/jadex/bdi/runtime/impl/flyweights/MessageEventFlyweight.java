package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MMessageEventFlyweight;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IMessageEventListener;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.MessageEventRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for a message event.
 */
public class MessageEventFlyweight extends ProcessableElementFlyweight implements IMessageEvent
{
	//-------- constructors --------
	
	/**
	 *  Create a new message event flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private MessageEventFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static MessageEventFlyweight getMessageEventFlyweight(IOAVState state, Object scope, Object handle)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		MessageEventFlyweight ret = (MessageEventFlyweight)ip.getFlyweightCache(IMessageEvent.class, new Tuple(IMessageEvent.class, handle));
		if(ret==null)
		{
			ret = new MessageEventFlyweight(state, scope, handle);
			ip.putFlyweightCache(IMessageEvent.class, new Tuple(IMessageEvent.class, handle), ret);
		}
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the native (platform specific) message object.
	 */
	public Object getMessage()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.messageevent_has_nativemessage);
				}
			};
			return invoc.object;
		}
		else
		{
			return getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.messageevent_has_nativemessage);
		}
	}

	/**
	 *  Get the message type.
	 *  @return The message type.
	 */
	public MessageType getMessageType()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object	= MessageEventRules.getMessageEventType(getState(),
						getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model));
				}
			};
			return (MessageType)invoc.object;
		}
		else
		{
			return MessageEventRules.getMessageEventType(getState(),
				getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model));
		}
	}
	
	/**
	 *  Create a reply to this message event.
	 *  @param msgeventtype	The message event type.
	 *  @return The reply event.
	 * /
	public IMessageEvent createReply(final String msgeventtype)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					IMessageEvent	reply	= EventbaseFlyweight.createMessageEvent(getState(), getScope(), msgeventtype);
					MessageEventRules.initializeReply(getState(), getScope(), getHandle(), ((ElementFlyweight)reply).getHandle());
					object = reply;
				}
			};
			return (IMessageEvent)invoc.object;
		}
		else
		{
			IMessageEvent	reply	= EventbaseFlyweight.createMessageEvent(getState(), getScope(), msgeventtype);
			MessageEventRules.initializeReply(getState(), getScope(), getHandle(), ((ElementFlyweight)reply).getHandle());
			return reply;
		}
	}*/

	/**
	 *  Get the filter to wait for a reply.
	 *  @return The filter.
	 * /
	public IFilter getFilter()
	{
		throw new UnsupportedOperationException();
	}*/
	
	//-------- listeners --------
	
	/**
	 *  Add a message event listener.
	 *  @param listener The message event listener.
	 */
	public void addMessageEventListener(final IMessageEventListener listener)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					addEventListener(listener, getHandle());
				}
			};
		}
		else
		{
			addEventListener(listener, getHandle());
		}
	}
	
	/**
	 *  Remove a message event listener.
	 *  @param listener The message event listener.
	 */
	public void removeMessageEventListener(final IMessageEventListener listener)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					removeEventListener(listener, getHandle(), false);
				}
			};
		}
		else
		{
			removeEventListener(listener, getHandle(), false);
		}
	}
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object = new MMessageEventFlyweight(getState(), mscope, me);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MMessageEventFlyweight(getState(), mscope, me);
		}
	}
}
