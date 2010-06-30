package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.runtime.IEAMessageEvent;
import jadex.bdi.runtime.IMessageEventListener;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.MessageEventRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.MessageType;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for a message event.
 */
public class EAMessageEventFlyweight extends EAProcessableElementFlyweight implements IEAMessageEvent
{
	//-------- attributes --------
	
	/** The cached message type. */
	protected MessageType mt;
	
	//-------- constructors --------
	
	/**
	 *  Create a new message event flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private EAMessageEventFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
		this.mt = MessageEventRules.getMessageEventType(getState(),
			getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model));
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static EAMessageEventFlyweight getMessageEventFlyweight(IOAVState state, Object scope, Object handle)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EAMessageEventFlyweight ret = (EAMessageEventFlyweight)ip.getFlyweightCache(IEAMessageEvent.class).get(handle);
		if(ret==null)
		{
			ret = new EAMessageEventFlyweight(state, scope, handle);
			ip.getFlyweightCache(IEAMessageEvent.class).put(handle, ret);
		}
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the native (platform specific) message object.
	 */
	public IFuture getMessage()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.messageevent_has_nativemessage));
				}
			});
		}
		else
		{
			ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.messageevent_has_nativemessage));
		}
		
		return ret;
	}

	/**
	 *  Get the message type.
	 *  @return The message type.
	 */
	public MessageType getMessageType()
	{
		return mt;
		
//		final Future ret = new Future();
//		
//		if(getInterpreter().isExternalThread())
//		{
//			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
//			{
//				public void run()
//				{
//					ret.setResult(MessageEventRules.getMessageEventType(getState(),
//						getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model)));
//				}
//			});
//		}
//		else
//		{
//			ret.setResult(MessageEventRules.getMessageEventType(getState(),
//				getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model)));
//		}
//		
//		return ret;
	}
	
	/**
	 *  Create a reply to this message event.
	 *  @param msgeventtype	The message event type.
	 *  @return The reply event.
	 * /
	public IMessageEvent createReply(final String msgeventtype)
	{
		if(getInterpreter().isExternalThread())
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
	public IFuture addMessageEventListener(final IMessageEventListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					addEventListener(listener, getHandle());
					ret.setResult(null);
				}
			});
		}
		else
		{
			addEventListener(listener, getHandle());
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove a message event listener.
	 *  @param listener The message event listener.
	 */
	public IFuture removeMessageEventListener(final IMessageEventListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					removeEventListener(listener, getHandle(), false);
					ret.setResult(null);
				}
			});
		}
		else
		{
			removeEventListener(listener, getHandle(), false);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 * /
	public IMElement getModelElement()
	{
		if(getInterpreter().isExternalThread())
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
	}*/
}

