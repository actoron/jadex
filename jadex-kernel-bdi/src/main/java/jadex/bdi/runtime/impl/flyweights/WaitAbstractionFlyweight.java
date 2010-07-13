package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.runtime.IExternalCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IWaitAbstraction;
import jadex.bdi.runtime.impl.FlyweightFunctionality;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for a waitabstraction.
 */
public class WaitAbstractionFlyweight extends ElementFlyweight implements IWaitAbstraction
{
	//-------- constructors --------
	
	/**
	 *  Create a new waitabstraction flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The element handle.
	 */
	protected WaitAbstractionFlyweight(IOAVState state, Object scope, Object handle)
	{
		// Todo: no more lazy wa creation. is this correct??? (required for plans/timeoutexception testcase)
		super(state, scope, handle!=null? handle: state.createObject(OAVBDIRuntimeModel.waitabstraction_type));
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static WaitAbstractionFlyweight getWaitAbstractionFlyweight(IOAVState state, Object scope, Object handle)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		WaitAbstractionFlyweight ret = (WaitAbstractionFlyweight)ip.getFlyweightCache(IWaitAbstraction.class).get(handle);
		if(ret==null)
		{
			ret = new WaitAbstractionFlyweight(state, scope, handle);
			ip.getFlyweightCache(IWaitAbstraction.class).put(handle, ret);
		}
		return ret;
	}
	
	//-------- adder methods --------
	
	/**
	 *  Add a message event.
	 *  @param type The type.
	 */
	public IWaitAbstraction addMessageEvent(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					FlyweightFunctionality.addMessageEvent(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			FlyweightFunctionality.addMessageEvent(wa, type, getState(), getScope());		
			return this;
		}
	}

	/**
	 * Add a message event reply.
	 * @param me The message event.
	 */
	public IWaitAbstraction addReply(final IMessageEvent me)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					FlyweightFunctionality.addReply(wa, (ElementFlyweight)me, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			FlyweightFunctionality.addReply(wa, (ElementFlyweight)me, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add an internal event.
	 *  @param type The type.
	 */
	public IWaitAbstraction addInternalEvent(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					FlyweightFunctionality.addInternalEvent(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			FlyweightFunctionality.addInternalEvent(wa, type, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add a goal.
	 *  @param type The type.
	 */
	public IWaitAbstraction addGoal(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					FlyweightFunctionality.addGoal(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			FlyweightFunctionality.addGoal(wa, type, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add a Goal.
	 *  @param goal The goal.
	 */
	public IWaitAbstraction addGoal(final IGoal goal)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					FlyweightFunctionality.addGoal(wa, (ElementFlyweight)goal, getState(), getScope());
				}
			};
			return this;		
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			FlyweightFunctionality.addGoal(wa, (ElementFlyweight)goal, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add a fact changed.
	 */
	public IWaitAbstraction addFactChanged(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					FlyweightFunctionality.addFactChanged(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			FlyweightFunctionality.addFactChanged(wa, type, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add a fact added.
	 */
	public IWaitAbstraction addFactAdded(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					FlyweightFunctionality.addFactAdded(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			FlyweightFunctionality.addFactAdded(wa, type, getState(), getScope());
			return this;
		}
	}


	/**
	 *  Add a fact added.
	 */
	public IWaitAbstraction addFactRemoved(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					FlyweightFunctionality.addFactRemoved(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			FlyweightFunctionality.addFactRemoved(wa, type, getState(), getScope());
			return this;
		}
	}
	
	/**
	 *  Add a condition.
	 *  @param condition the condition name.
	 */
	public IWaitAbstraction addCondition(final String condition)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					FlyweightFunctionality.addCondition(wa, condition, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			FlyweightFunctionality.addCondition(wa, condition, getState(), getScope());
			return this;
		}
	}


	/**
	 *  Add an external condition.
	 *  @param condition The condition.
	 */
	public IWaitAbstraction addExternalCondition(final IExternalCondition condition)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					FlyweightFunctionality.addExternalCondition(wa, condition, getState(), getScope());
				}
			};
			return this;		
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			FlyweightFunctionality.addExternalCondition(wa, condition, getState(), getScope());
			return this;
		}
	}

	//-------- remover methods --------

	/**
	 *  Remove a message event.
	 *  @param type The type.
	 */
	public void removeMessageEvent(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					FlyweightFunctionality.removeMessageEvent(getState(), getScope(), type, getWaitAbstraction());
				}
			};
		}
		else
		{
			FlyweightFunctionality.removeMessageEvent(getState(), getScope(), type, getWaitAbstraction());
		}
	}

	/**
	 *  Remove a message event reply.
	 *  @param me The message event.
	 */
	public void removeReply(final IMessageEvent me)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					FlyweightFunctionality.removeReply(getState(), getScope(), (ElementFlyweight)me, getWaitAbstraction());
				}
			};
		}
		else
		{
			FlyweightFunctionality.removeReply(getState(), getScope(), (ElementFlyweight)me, getWaitAbstraction());
		}
	}

	/**
	 *  Remove an internal event.
	 *  @param type The type.
	 */
	public void removeInternalEvent(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					FlyweightFunctionality.removeInternalEvent(getState(), getScope(), type, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			FlyweightFunctionality.removeInternalEvent(getState(), getScope(), type, wa);
		}
	}

	/**
	 *  Remove a goal.
	 *  @param type The type.
	 */
	public void removeGoal(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					FlyweightFunctionality.removeGoal(getState(), getScope(), type, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			FlyweightFunctionality.removeGoal(getState(), getScope(), type, wa);
		}
	}

	/**
	 *  Remove a goal.
	 *  @param goal The goal.
	 */
	public void removeGoal(final IGoal goal)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					FlyweightFunctionality.removeGoal(getState(), (ElementFlyweight)goal, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			FlyweightFunctionality.removeGoal(getState(), (ElementFlyweight)goal, wa);
		}
	}
	
	/**
	 *  Remove a fact changed.
	 *  @param belief The belief or beliefset.
	 */
	public void removeFactChanged(final String belief)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					FlyweightFunctionality.removeFactChanged(getState(), getScope(), belief, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			FlyweightFunctionality.removeFactChanged(getState(), getScope(), belief, wa);
		}
	}
	
	/**
	 *  Remove a fact added.
	 *  @param beliefset The beliefset.
	 */
	public void removeFactAdded(final String beliefset)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					FlyweightFunctionality.removeFactAdded(getState(), getScope(), beliefset, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			FlyweightFunctionality.removeFactAdded(getState(), getScope(), beliefset, wa);
		}
	}


	/**
	 *  Remove a fact removed.
	 *  @param beliefset The beliefset.
	 */
	public void removeFactRemoved(final String beliefset)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					FlyweightFunctionality.removeFactRemoved(getState(), getScope(), beliefset, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			FlyweightFunctionality.removeFactRemoved(getState(), getScope(), beliefset, wa);
		}
	}
	
	/**
	 *  Remove a condition.
	 *  @param type The condition.
	 */
	public void removeCondition(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					FlyweightFunctionality.removeCondition(getState(), getScope(), type, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			FlyweightFunctionality.removeCondition(getState(), getScope(), type, wa);
		}
	}
	
	/**
	 *  Remove an external condition.
	 *  @param condition The condition.
	 */
	public void removeExternalCondition(final IExternalCondition condition)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					FlyweightFunctionality.removeExternalCondition(getState(), condition, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			FlyweightFunctionality.removeExternalCondition(getState(), condition, wa);
		}
	}

	/**
	 *  Get or create the waitabstraction.
	 *  @return The waitabstraction.
	 */
	protected Object getWaitAbstraction()
	{
		return getHandle();
//		return getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_preparedwaitabstraction);
	}
	
	/**
	 *  Create the waitabstraction.
	 *  @return The waitabstraction.
	 */
	protected Object createWaitAbstraction()
	{
		setHandle(getState().createObject(OAVBDIRuntimeModel.waitabstraction_type));
//		getState().setAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_preparedwaitabstraction, wa);
		return getHandle();
	}
	
	/**
	 *  Get or create the waitabstraction.
	 *  @return The waitabstraction.
	 */
	protected Object getOrCreateWaitAbstraction()
	{
		Object wa = getWaitAbstraction();
		if(wa==null)
			wa = createWaitAbstraction();
		return wa;
	}
}
