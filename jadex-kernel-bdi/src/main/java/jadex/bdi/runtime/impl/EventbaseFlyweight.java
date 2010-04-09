package jadex.bdi.runtime.impl;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IEventbase;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IInternalEventListener;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IMessageEventListener;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.InternalEventRules;
import jadex.bdi.runtime.interpreter.MessageEventRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.Tuple;
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
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EventbaseFlyweight ret = (EventbaseFlyweight)ip.getFlyweightCache(IEventbase.class).get(new Tuple(IEventbase.class, scope));
		if(ret==null)
		{
			ret = new EventbaseFlyweight(state, scope);
			ip.getFlyweightCache(IEventbase.class).put(new Tuple(IEventbase.class, scope), ret);
		}
		return ret;
	}
	
	//-------- methods --------

	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	// changed signature for javaflow, removed final
	public void	sendMessage(IMessageEvent me)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation(me)
			{
				public void run()
				{
					Object revent = ((MessageEventFlyweight)arg).getHandle();
					MessageEventRules.sendMessage(getState(), getScope(), revent);
				}
			};
		}
		else
		{
			Object revent = ((MessageEventFlyweight)me).getHandle();
			MessageEventRules.sendMessage(getState(), getScope(), revent);
		}
	}

	/**
	 *  Dispatch an event.
	 *  @param event The event.
	 */
	// changed signature for javaflow, removed final
	public void dispatchInternalEvent(IInternalEvent event)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation(event)
			{
				public void run()
				{
					Object revent = ((InternalEventFlyweight)arg).getHandle();
					InternalEventRules.adoptInternalEvent(getState(), getScope(), revent);
				}
			};
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			Object revent = ((InternalEventFlyweight)event).getHandle();
			InternalEventRules.adoptInternalEvent(getState(), getScope(), revent);
			getInterpreter().endMonitorConsequences();
		}
	}

	/**
	 *  Create a new message event.
	 *  @return The new message event.
	 */
	public IMessageEvent createMessageEvent(String type)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(type)
			{
				public void run()
				{
					object	= createMessageEvent(getState(), getScope(), (String) arg);
				}
			};
			return (IMessageEvent)invoc.object;
		}
		else
		{
			return createMessageEvent(getState(), getScope(), type);
		}
	}

	/**
	 *  Create a reply to a message event.
	 *  @param event	The received message event.
	 *  @param msgeventtype	The reply message event type.
	 *  @return The reply event.
	 */
	// changed signature for javaflow, removed 2 final
	public IMessageEvent createReply(IMessageEvent event, String msgeventtype)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation(new Object[]{event, msgeventtype})
			{
				public void run()
				{
					IMessageEvent	reply	= EventbaseFlyweight.createMessageEvent(getState(), getScope(), (String) args[1]);
					MessageEventRules.initializeReply(getState(), getScope(), ((ElementFlyweight)args[0]).getHandle(), ((ElementFlyweight)reply).getHandle());
					object = reply;
				}
			};
			return (IMessageEvent)invoc.object;
		}
		else
		{
			IMessageEvent	reply	= EventbaseFlyweight.createMessageEvent(getState(), getScope(), msgeventtype);
			MessageEventRules.initializeReply(getState(), getScope(), ((ElementFlyweight)event).getHandle(), ((ElementFlyweight)reply).getHandle());
			return reply;
		}
	}
	
	
	/**
	 *  Create a new intenal event.
	 *  @return The new intenal event.
	 */
	// changed signature for javaflow, removed final
	public IInternalEvent createInternalEvent(final String ref)
	{
		if(getInterpreter().isExternalThread())
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
	// changed signature for javaflow, removed 2 final
	public void addInternalEventListener(String type, IInternalEventListener listener)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation(new Object[]{type, listener})
			{
				public void run()
				{			
					Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					Object mevent = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_internalevents, args[0]);
					if(mevent==null)
						throw new RuntimeException("Unknown event type: "+args[0]);
					
					addEventListener(args[1], mevent);
				}
			};
		}
		else
		{
			Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			Object mevent = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_internalevents, type);
			if(mevent==null)
				throw new RuntimeException("Unknown event type: "+type);
			
			addEventListener(listener, mevent);
		}
	}
	
	/**
	 *  Remove a internal event listener.
	 *  @param type	The internal event type.
	 *  @param listener The internal event listener.
	 */
	// changed signature for javaflow, removed 2 final
	public void removeInternalEventListener(String type, IInternalEventListener listener)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation(new Object[]{type, listener})
			{
				public void run()
				{
					Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					Object mevent = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_internalevents, args[0]);
					if(mevent==null)
						throw new RuntimeException("Unknown event type: "+args[0]);
				
					removeEventListener(args[1], mevent, false);
				}
			};
		}
		else
		{
			Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			Object mevent = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_internalevents, type);
			if(mevent==null)
				throw new RuntimeException("Unknown event type: "+type);
			
			removeEventListener(listener, mevent, false);
		}
	}
	
	/**
	 *  Add a message event listener.
	 *  @param type	The message event type.
	 *  @param listener The message event listener.
	 */
	// changed signature for javaflow, removed 2 final
	public void addMessageEventListener(String type, IMessageEventListener listener)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation(new Object[]{type, listener})
			{
				public void run()
				{
					Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					Object mevent = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_messageevents, args[0]);
					if(mevent==null)
						throw new RuntimeException("Event not found: "+args[0]);

					addEventListener(args[1], mevent);
				}
			};
		}
		else
		{
			Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			Object mevent = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_messageevents, type);
			if(mevent==null)
				throw new RuntimeException("Event not found: "+type);
			
			addEventListener(listener, mevent);
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
		if(getInterpreter().isExternalThread())
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
		if(getInterpreter().isExternalThread())
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
	
	/**
	 *  Remove a message event listener.
	 *  @param type	The message event type.
	 *  @param listener The message event listener.
	 */
	// changed signature for javaflow, removed 2 final
	public void removeMessageEventListener(String type, IMessageEventListener listener)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation(new Object[]{type, listener})
			{
				public void run()
				{
					Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					Object mevent = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_messageevents, args[0]);
					if(mevent==null)
						throw new RuntimeException("Event not found: "+args[0]);
					
					removeEventListener(args[1], mevent, false);
				}
			};
		}
		else
		{
			Object mcapa = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			Object mevent = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_messageevents, type);
			if(mevent==null)
				throw new RuntimeException("Event not found: "+type);
			
			removeEventListener(listener, mevent, false);
		}
	}
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IComponentIdentifier createComponentIdentifier(String name)
	{
		return createComponentIdentifier(name, true, null);
	}
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IComponentIdentifier createComponentIdentifier(String name, boolean local)
	{
		return createComponentIdentifier(name, local, null);
	}
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IComponentIdentifier createComponentIdentifier(final String name, final boolean local, final String[] addresses)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation ai = new AgentInvocation()
			{
				public void run()
				{
					IComponentManagementService cms = (IComponentManagementService)getInterpreter().getAgentAdapter().getServiceContainer().getService(IComponentManagementService.class);	
					object = cms.createComponentIdentifier(name, local, addresses);
				}
			};
			return (IComponentIdentifier)ai.object;
		}
		else
		{
			IComponentManagementService cms = (IComponentManagementService)getInterpreter().getAgentAdapter().getServiceContainer().getService(IComponentManagementService.class);	
			return cms.createComponentIdentifier(name, local, addresses);
		}
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
	}*/
	
	//-------- helper methods --------

	/**
	 *  Create an internal event of a given type but does not add to state.
	 *  @param state The state.
	 *  @param rcap The scope.
	 *  @param type The type.
	 */
	public static IInternalEvent createInternalEvent(IOAVState state, Object rcap, String type)
	{
		IInternalEvent	ret;
		
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.internalevent_type, rcap, state);
		Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(state.containsKey(mscope, OAVBDIMetaModel.capability_has_internalevents, scope[0]))
		{
			Object	mevent = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_internalevents, scope[0]);
			Object	revent = InternalEventRules.instantiateInternalEvent(state, scope[1], mevent, null, null, null, null);
			ret	= InternalEventFlyweight.getInternalEventFlyweight(state, scope[1], revent);
		}
		else
		{
			throw new RuntimeException("No such message event: "+scope[0]+" in "+scope[1]);
		}
	
		return ret;
	}
	
	/**
	 *  Create an message event of a given type but does not add to state.
	 *  @param state The state.
	 *  @param rcap The scope.
	 *  @param type The type.
	 */
	public static IMessageEvent createMessageEvent(IOAVState state, Object rcap, String type)
	{
		IMessageEvent	ret;
		
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.messageevent_type, rcap, state);
		Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(state.containsKey(mscope, OAVBDIMetaModel.capability_has_messageevents, scope[0]))
		{
			Object	mevent = state.getAttributeValue(mscope, OAVBDIMetaModel.capability_has_messageevents, scope[0]);
			Object	revent = MessageEventRules.instantiateMessageEvent(state, scope[1], mevent, null, null, null, null);
			ret	= MessageEventFlyweight.getMessageEventFlyweight(state, scope[1], revent);
		}
		else
		{
			throw new RuntimeException("No such message event: "+scope[0]+" in "+scope[1]);
		}
	
		return ret;
	}
}
