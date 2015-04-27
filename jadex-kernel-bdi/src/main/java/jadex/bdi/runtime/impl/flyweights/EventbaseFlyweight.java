package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.model.IMElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MEventbaseFlyweight;
import jadex.bdi.runtime.IEventbase;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IInternalEventListener;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IMessageEventListener;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.InternalEventRules;
import jadex.bdi.runtime.interpreter.MessageEventRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
import jadex.commons.future.IFuture;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for the eventbase.
 */
public class EventbaseFlyweight extends ElementFlyweight implements IEventbase
{
	//-------- constructors --------
	
	/**
	 *  Create a new goalbase flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private EventbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static EventbaseFlyweight getEventbaseFlyweight(IOAVState state, Object scope)
	{
		IInternalBDIAgentFeature ip = BDIAgentFeature.getInterpreter(state);
		EventbaseFlyweight ret = (EventbaseFlyweight)ip.getFlyweightCache(IEventbase.class, new Tuple(IEventbase.class, scope));
		if(ret==null)
		{
			ret = new EventbaseFlyweight(state, scope);
			ip.putFlyweightCache(IEventbase.class, new Tuple(IEventbase.class, scope), ret);
		}
		return ret;
	}
	
	//-------- methods --------

	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public IFuture	sendMessage(IMessageEvent me)
	{
		return sendMessage(me, null);
	}
	
	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public IFuture	sendMessage(IMessageEvent me, final byte[] codecids)
	{
		IFuture	ret;
		if(isExternalThread())
		{
			AgentInvocation	invoc	= new AgentInvocation(me)
			{
				public void run()
				{
					object	= MessageEventRules.sendMessage(getState(), getScope(), ((ElementFlyweight)arg).getHandle(), codecids);
				}
			};
			ret	= (IFuture)invoc.object;
		}
		else
		{
			ret	= MessageEventRules.sendMessage(getState(), getScope(), ((ElementFlyweight)me).getHandle(), codecids);
		}
		return ret;
	}

	/**
	 *  Dispatch an event.
	 *  @param event The event.
	 */
	// changed signature for javaflow, removed final
	public void dispatchInternalEvent(IInternalEvent event)
	{
		if(isExternalThread())
		{
			new AgentInvocation(event)
			{
				public void run()
				{
					InternalEventRules.adoptInternalEvent(getState(), getScope(), ((InternalEventFlyweight)arg).getHandle());
				}
			};
		}
		else
		{
			getBDIFeature().startMonitorConsequences();
			InternalEventRules.adoptInternalEvent(getState(), getScope(), ((InternalEventFlyweight)event).getHandle());
			getBDIFeature().endMonitorConsequences();
		}
	}

	/**
	 *  Create a new message event.
	 *  @return The new message event.
	 */
	public IMessageEvent createMessageEvent(String type)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(type)
			{
				public void run()
				{
					object	= SFlyweightFunctionality.createMessageEvent(getState(), getScope(), (String)arg);
				}
			};
			return (IMessageEvent)invoc.object;
		}
		else
		{
			return (IMessageEvent)SFlyweightFunctionality.createMessageEvent(getState(), getScope(), type);
		}
	}

	/**
	 *  Create a reply to a message event.
	 *  @param event	The received message event.
	 *  @param msgeventtype	The reply message event type.
	 *  @return The reply event.
	 */
	public IMessageEvent createReply(final IMessageEvent event, final String msgeventtype)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(new Object[]{event, msgeventtype})
			{
				public void run()
				{
					ElementFlyweight reply = (ElementFlyweight)SFlyweightFunctionality.createMessageEvent(getState(), getScope(), msgeventtype);
					object = MessageEventFlyweight.getMessageEventFlyweight(getState(), getScope(), MessageEventRules.initializeReply(getState(), getScope(), ((ElementFlyweight)event).getHandle(), ((ElementFlyweight)reply).getHandle()));
				}
			};
			return (IMessageEvent)invoc.object;
		}
		else
		{
			ElementFlyweight reply = (ElementFlyweight)SFlyweightFunctionality.createMessageEvent(getState(), getScope(), msgeventtype);
			return MessageEventFlyweight.getMessageEventFlyweight(getState(), getScope(), MessageEventRules.initializeReply(getState(), getScope(), ((ElementFlyweight)event).getHandle(), ((ElementFlyweight)reply).getHandle()));
		}
	}
	
	
	/**
	 *  Create a new intenal event.
	 *  @return The new intenal event.
	 */
	public IInternalEvent createInternalEvent(final String ref)
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object[] scope = AgentRules.resolveCapability(ref, OAVBDIMetaModel.internalevent_type, getScope(), getState());
					Object revent = InternalEventRules.createInternalEvent(getState(), scope[1], (String)scope[0]);
					object = InternalEventFlyweight.getInternalEventFlyweight(getState(), scope[1], revent);
				}
			};
			return (IInternalEvent)invoc.object;
		}
		else
		{
			Object[] scope = AgentRules.resolveCapability(ref, OAVBDIMetaModel.internalevent_type, getScope(), getState());
			Object revent = InternalEventRules.createInternalEvent(getState(), scope[1], (String)scope[0]);
			return InternalEventFlyweight.getInternalEventFlyweight(getState(), scope[1], revent);
		}
	}

	/**
	 *  Create a legacy internal event (not explicitly defined in ADF).
	 *  @return The new internal event.
	 *  @deprecated Convenience method for easy conversion to the new Jadex version.
	 *  Will be removed in later releases.
	 * /
	public IInternalEvent createInternalEvent(String type, Object content);*/

	/**
	 *  Register a new event model.
	 *  @param mevent The event model.
	 * /
	public void registerEvent(IMEvent mevent)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Register a new event reference model.
	 *  @param meventref The event reference model.
	 * /
	public void registerEventReference(IMEventReference meventref)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Deregister an event model.
	 *  @param mevent The event model.
	 * /
	public void deregisterEvent(IMEvent mevent)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Deregister an event reference model.
	 *  @param meventref The event reference model.
	 * /
	public void deregisterEventReference(IMEventReference meventref)
	{
		throw new UnsupportedOperationException();
	}*/
	
	//-------- listeners --------

	/**
	 *  Add a internal event listener.
	 *  @param type	The internal event type.
	 *  @param listener The internal event listener.
	 */
	public void addInternalEventListener(final String type, final IInternalEventListener listener)
	{
		if(isExternalThread())
		{
			new AgentInvocation(new Object[]{type, listener})
			{
				public void run()
				{			
					Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_internalevents);
					addEventListener(listener, mevent);
				}
			};
		}
		else
		{
			Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_internalevents);
			addEventListener(listener, mevent);
		}
	}
	
	/**
	 *  Remove a internal event listener.
	 *  @param type	The internal event type.
	 *  @param listener The internal event listener.
	 */
	public void removeInternalEventListener(final String type, final IInternalEventListener listener)
	{
		if(isExternalThread())
		{
			new AgentInvocation(new Object[]{type, listener})
			{
				public void run()
				{
					Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_internalevents);
					removeEventListener(args[1], mevent, false);
				}
			};
		}
		else
		{
			Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_internalevents);
			removeEventListener(listener, mevent, false);
		}
	}
	
	/**
	 *  Add a message event listener.
	 *  @param type	The message event type.
	 *  @param listener The message event listener.
	 */
	public void addMessageEventListener(final String type, final IMessageEventListener listener)
	{
		if(isExternalThread())
		{
			new AgentInvocation(new Object[]{type, listener})
			{
				public void run()
				{
					Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_messageevents);
					addEventListener(args[1], mevent);
				}
			};
		}
		else
		{
			Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_messageevents);
			addEventListener(listener, mevent);
		}
	}
	
	/**
	 *  Remove a message event listener.
	 *  @param type	The message event type.
	 *  @param listener The message event listener.
	 */
	public void removeMessageEventListener(final String type, final IMessageEventListener listener)
	{
		if(isExternalThread())
		{
			new AgentInvocation(new Object[]{type, listener})
			{
				public void run()
				{
					Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_messageevents);
					removeEventListener(listener, mevent, false);
				}
			};
		}
		else
		{
			Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_messageevents);
			removeEventListener(listener, mevent, false);
		}
	}
	
	/**
	 *  Register a conversation or reply_with to be able
	 *  to send back answers to the source capability.
	 *  @param msgevent The message event.
	 *  todo: indexing for msgevents for speed.
	 */
	public void registerMessageEvent(final IMessageEvent mevent)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					MessageEventFlyweight mef = (MessageEventFlyweight)mevent;
					MessageEventRules.registerMessageEvent(getState(), mef.getHandle(), mef.getScope());
				}
			};
		}
		else
		{
			MessageEventFlyweight mef = (MessageEventFlyweight)mevent;
			MessageEventRules.registerMessageEvent(getState(), mef.getHandle(), mef.getScope());
		}
	}
	
	/**
	 *  Remove a registered message event.
	 *  @param msgevent The message event.
	 */
	public void deregisterMessageEvent(final IMessageEvent mevent)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					MessageEventFlyweight mef = (MessageEventFlyweight)mevent;
					MessageEventRules.deregisterMessageEvent(getState(), mef.getHandle(), mef.getScope());

				}
			};
		}
		else
		{
			MessageEventFlyweight mef = (MessageEventFlyweight)mevent;
			MessageEventRules.deregisterMessageEvent(getState(), mef.getHandle(), mef.getScope());
		}
	}
	
//	/**
//	 *  Create component identifier.
//	 *  @param name The name.
//	 *  @param local True for local name.
//	 *  @param addresses The addresses.
//	 *  @return The new component identifier.
//	 */
//	public IComponentIdentifier createComponentIdentifier(String name)
//	{
//		return createComponentIdentifier(name, true, null);
//	}
//	
//	/**
//	 *  Create component identifier.
//	 *  @param name The name.
//	 *  @param parent The parent identifier.
//	 *  @return The new component identifier.
//	 */
//	public IComponentIdentifier createComponentIdentifier(final String name, final IComponentIdentifier parent)
//	{
//		if(isExternalThread())
//		{
//			AgentInvocation ai = new AgentInvocation()
//			{
//				public void run()
//				{
//					object = getInterpreter().getCMS().createComponentIdentifier(name, parent, parent.getAddresses());
//				}
//			};
//			return (IComponentIdentifier)ai.object;
//		}
//		else
//		{
//			return getInterpreter().getCMS().createComponentIdentifier(name, parent, parent.getAddresses());
//		}
//	}
//	
//	/**
//	 *  Create component identifier.
//	 *  @param name The name.
//	 *  @param local True for local name.
//	 *  @param addresses The addresses.
//	 *  @return The new component identifier.
//	 */
//	public IComponentIdentifier createComponentIdentifier(String name, boolean local)
//	{
//		return createComponentIdentifier(name, local, null);
//	}
//	
//	/**
//	 *  Create component identifier.
//	 *  @param name The name.
//	 *  @param local True for local name.
//	 *  @param addresses The addresses.
//	 *  @return The new component identifier.
//	 */
//	public IComponentIdentifier createComponentIdentifier(final String name, final boolean local, final String[] addresses)
//	{
//		if(isExternalThread())
//		{
//			AgentInvocation ai = new AgentInvocation()
//			{
//				public void run()
//				{
//					object = getInterpreter().getCMS().createComponentIdentifier(name, local, addresses);
//				}
//			};
//			return (IComponentIdentifier)ai.object;
//		}
//		else
//		{
//			return getInterpreter().getCMS().createComponentIdentifier(name, local, addresses);
//		}
//	}
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object = new MEventbaseFlyweight(getState(), mscope);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MEventbaseFlyweight(getState(), mscope);
		}
	}
	
}
