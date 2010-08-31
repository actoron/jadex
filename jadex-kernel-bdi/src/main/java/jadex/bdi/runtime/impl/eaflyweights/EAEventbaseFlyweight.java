package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MEventbaseFlyweight;
import jadex.bdi.runtime.IEAEventbase;
import jadex.bdi.runtime.IEAInternalEvent;
import jadex.bdi.runtime.IEAMessageEvent;
import jadex.bdi.runtime.IInternalEventListener;
import jadex.bdi.runtime.IMessageEventListener;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.InternalEventRules;
import jadex.bdi.runtime.interpreter.MessageEventRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentManagementService;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Tuple;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.rules.state.IOAVState;


/**
 *  Flyweight for the eventbase.
 */
public class EAEventbaseFlyweight extends ElementFlyweight implements IEAEventbase
{
//-------- constructors --------
	
	/**
	 *  Create a new goalbase flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private EAEventbaseFlyweight(IOAVState state, Object scope)
	{
		super(state, scope, scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static EAEventbaseFlyweight getEventbaseFlyweight(IOAVState state, Object scope)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EAEventbaseFlyweight ret = (EAEventbaseFlyweight)ip.getFlyweightCache(IEAEventbase.class, new Tuple(IEAEventbase.class, scope));
		if(ret==null)
		{
			ret = new EAEventbaseFlyweight(state, scope);
			ip.putFlyweightCache(IEAEventbase.class, new Tuple(IEAEventbase.class, scope), ret);
		}
		return ret;
	}
	
	//-------- methods --------

	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public IFuture sendMessage(final IEAMessageEvent me)
	{
		IFuture	ret;
		if(getInterpreter().isExternalThread())
		{
			final Future	future	= new Future();
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object revent = ((EAMessageEventFlyweight)me).getHandle();
					MessageEventRules.sendMessage(getState(), getScope(), revent).addResultListener(new DelegationResultListener(future));
				}
			});
			ret	= future;
		}
		else
		{
			Object revent = ((EAMessageEventFlyweight)me).getHandle();
			ret	= MessageEventRules.sendMessage(getState(), getScope(), revent);
		}
		
		return ret;
	}

	/**
	 *  Dispatch an event.
	 *  @param event The event.
	 */
	public IFuture dispatchInternalEvent(final IEAInternalEvent event)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object revent = ((ElementFlyweight)event).getHandle();
					InternalEventRules.adoptInternalEvent(getState(), getScope(), revent);
					ret.setResult(null);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			Object revent = ((ElementFlyweight)event).getHandle();
			InternalEventRules.adoptInternalEvent(getState(), getScope(), revent);
			getInterpreter().endMonitorConsequences();
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Create a new message event.
	 *  @return The new message event.
	 */
	public IFuture createMessageEvent(final String type)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(SFlyweightFunctionality.createMessageEvent(getState(), getScope(), type, true));
				}
			});
		}
		else
		{
			ret.setResult(SFlyweightFunctionality.createMessageEvent(getState(), getScope(), type, true));
		}
		
		return ret;
	}

	/**
	 *  Create a reply to a message event.
	 *  @param event	The received message event.
	 *  @param msgeventtype	The reply message event type.
	 *  @return The reply event.
	 */
	public IFuture createReply(final IEAMessageEvent event, final String msgeventtype)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					IEAMessageEvent	reply	= (IEAMessageEvent)SFlyweightFunctionality.createMessageEvent(getState(), getScope(), msgeventtype, true);
					MessageEventRules.initializeReply(getState(), getScope(), ((ElementFlyweight)event).getHandle(), ((ElementFlyweight)reply).getHandle());
					ret.setResult(reply);
				}
			});
		}
		else
		{
			IEAMessageEvent	reply	= (IEAMessageEvent)SFlyweightFunctionality.createMessageEvent(getState(), getScope(), msgeventtype, true);
			MessageEventRules.initializeReply(getState(), getScope(), ((ElementFlyweight)event).getHandle(), ((ElementFlyweight)reply).getHandle());
			ret.setResult(reply);
		}
		
		return ret;
	}
	
	
	/**
	 *  Create a new intenal event.
	 *  @return The new intenal event.
	 */
	public IFuture createInternalEvent(final String ref)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object[] scope = AgentRules.resolveCapability(ref, OAVBDIMetaModel.internalevent_type, getScope(), getState());
					Object revent = InternalEventRules.createInternalEvent(getState(), scope[1], (String)scope[0]);
					ret.setResult(EAInternalEventFlyweight.getInternalEventFlyweight(getState(), scope[1], revent));
				}
			});
		}
		else
		{
			Object[] scope = AgentRules.resolveCapability(ref, OAVBDIMetaModel.internalevent_type, getScope(), getState());
			Object revent = InternalEventRules.createInternalEvent(getState(), scope[1], (String)scope[0]);
			ret.setResult(EAInternalEventFlyweight.getInternalEventFlyweight(getState(), scope[1], revent));
		}
		
		return ret;
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
	public IFuture addInternalEventListener(final String type, final IInternalEventListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{			
					Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_internalevents);
					addEventListener(listener, mevent);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_internalevents);
			addEventListener(listener, mevent);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove a internal event listener.
	 *  @param type	The internal event type.
	 *  @param listener The internal event listener.
	 */
	public IFuture removeInternalEventListener(final String type, final IInternalEventListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_internalevents);
					removeEventListener(listener, mevent, false);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_internalevents);
			removeEventListener(listener, mevent, false);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Add a message event listener.
	 *  @param type	The message event type.
	 *  @param listener The message event listener.
	 */
	public IFuture addMessageEventListener(final String type, final IMessageEventListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_messageevents);
					addEventListener(listener, mevent);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_messageevents);
			addEventListener(listener, mevent);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove a message event listener.
	 *  @param type	The message event type.
	 *  @param listener The message event listener.
	 */
	public IFuture removeMessageEventListener(final String type, final IMessageEventListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_messageevents);
					removeEventListener(listener, mevent, false);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object mevent = SFlyweightFunctionality.checkElementType(getState(), getScope(), type, OAVBDIMetaModel.capability_has_messageevents);
			removeEventListener(listener, mevent, false);
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Register a conversation or reply_with to be able
	 *  to send back answers to the source capability.
	 *  @param msgevent The message event.
	 *  todo: indexing for msgevents for speed.
	 */
	public IFuture registerMessageEvent(final IEAMessageEvent mevent)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					EAMessageEventFlyweight mef = (EAMessageEventFlyweight)mevent;
					MessageEventRules.registerMessageEvent(getState(), mef.getHandle(), mef.getScope());
					ret.setResult(null);
				}
			});
		}
		else
		{
			EAMessageEventFlyweight mef = (EAMessageEventFlyweight)mevent;
			MessageEventRules.registerMessageEvent(getState(), mef.getHandle(), mef.getScope());
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Remove a registered message event.
	 *  @param msgevent The message event.
	 */
	public IFuture deregisterMessageEvent(final IEAMessageEvent mevent)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					EAMessageEventFlyweight mef = (EAMessageEventFlyweight)mevent;
					MessageEventRules.deregisterMessageEvent(getState(), mef.getHandle(), mef.getScope());
					ret.setResult(null);
				}
			});
		}
		else
		{
			EAMessageEventFlyweight mef = (EAMessageEventFlyweight)mevent;
			MessageEventRules.deregisterMessageEvent(getState(), mef.getHandle(), mef.getScope());
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Create component identifier.
	 *  @param name The name.
	 *  @param local True for local name.
	 *  @param addresses The addresses.
	 *  @return The new component identifier.
	 */
	public IFuture createComponentIdentifier(String name)
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
	public IFuture createComponentIdentifier(String name, boolean local)
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
	public IFuture createComponentIdentifier(final String name, final boolean local, final String[] addresses)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					SServiceProvider.getService(getInterpreter().getServiceProvider(), IComponentManagementService.class).addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							IComponentManagementService cms = (IComponentManagementService)result;
							ret.setResult(cms.createComponentIdentifier(name, local, addresses));
						}
					});
				}
			});
		}
		else
		{
			SServiceProvider.getService(getInterpreter().getServiceProvider(), IComponentManagementService.class).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					IComponentManagementService cms = (IComponentManagementService)result;
					ret.setResult(cms.createComponentIdentifier(name, local, addresses));
				}
			});
		}
		
		return ret;
	}
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object mscope = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					object = new MEventbaseFlyweight(getState(), mscope);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object mscope = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			return new MEventbaseFlyweight(getState(), mscope);
		}
	}
	
}
