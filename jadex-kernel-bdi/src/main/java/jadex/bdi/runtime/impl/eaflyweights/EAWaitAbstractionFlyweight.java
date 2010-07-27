package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.runtime.IEAGoal;
import jadex.bdi.runtime.IEAMessageEvent;
import jadex.bdi.runtime.IEAWaitAbstraction;
import jadex.bdi.runtime.IExternalCondition;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for a waitabstraction.
 */
public abstract class EAWaitAbstractionFlyweight extends ElementFlyweight implements IEAWaitAbstraction
{
	//-------- constructors --------
	
	/**
	 *  Create a new waitabstraction flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The element handle.
	 */
	protected EAWaitAbstractionFlyweight(IOAVState state, Object scope, Object handle)
	{
		// Todo: no more lazy wa creation. is this correct??? (required for plans/timeoutexception testcase)
		super(state, scope, handle!=null? handle: state.createObject(OAVBDIRuntimeModel.waitabstraction_type));
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 * /
	public static EAWaitAbstractionFlyweight getWaitAbstractionFlyweight(IOAVState state, Object scope, Object handle)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EAWaitAbstractionFlyweight ret = (EAWaitAbstractionFlyweight)ip.getFlyweightCache(IEAWaitAbstraction.class).get(handle);
		if(ret==null)
		{
			ret = new EAWaitAbstractionFlyweight(state, scope, handle);
			ip.getFlyweightCache(IEAWaitAbstraction.class).put(handle, ret);
		}
		return ret;
	}*/
	
	//-------- adder methods --------
	
	/**
	 *  Add a message event.
	 *  @param type The type.
	 */
	public IEAWaitAbstraction addMessageEvent(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addMessageEvent(wa, type, getState(), getScope());
				}
			});
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addMessageEvent(wa, type, getState(), getScope());		
		}
		
		return this;
	}

	/**
	 * Add a message event reply.
	 * @param me The message event.
	 */
	public IEAWaitAbstraction addReply(final IEAMessageEvent me)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addReply(wa, (ElementFlyweight)me, getState(), getScope());
				}
			});
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addReply(wa, (ElementFlyweight)me, getState(), getScope());
		}
		
		return this;
	}

	/**
	 *  Add an internal event.
	 *  @param type The type.
	 */
	public IEAWaitAbstraction addInternalEvent(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addInternalEvent(wa, type, getState(), getScope());
				}
			});
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addInternalEvent(wa, type, getState(), getScope());
		}
		
		return this;
	}

	/**
	 *  Add a goal.
	 *  @param type The type.
	 */
	public IEAWaitAbstraction addGoal(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addGoal(wa, type, getState(), getScope());
				}
			});
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addGoal(wa, type, getState(), getScope());
		}
		
		return this;
	}

	/**
	 *  Add a Goal.
	 *  @param goal The goal.
	 */
	public IEAWaitAbstraction addGoal(final IEAGoal goal)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addGoal(wa, (ElementFlyweight)goal, getState(), getScope());
				}
			});
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addGoal(wa, (ElementFlyweight)goal, getState(), getScope());
		}
		
		return this;
	}

	/**
	 *  Add a fact changed.
	 */
	public IEAWaitAbstraction addFactChanged(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addFactChanged(wa, type, getState(), getScope());
				}
			});
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addFactChanged(wa, type, getState(), getScope());
		}
		
		return this;
	}

	/**
	 *  Add a fact added.
	 */
	public IEAWaitAbstraction addFactAdded(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addFactAdded(wa, type, getState(), getScope());
				}
			});
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addFactAdded(wa, type, getState(), getScope());
		}
		
		return this;
	}


	/**
	 *  Add a fact added.
	 */
	public IEAWaitAbstraction addFactRemoved(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addFactRemoved(wa, type, getState(), getScope());
				}
			});
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addFactRemoved(wa, type, getState(), getScope());
		}
		
		return this;
	}
	
	/**
	 *  Add a condition.
	 *  @param condition the condition name.
	 */
	public IEAWaitAbstraction addCondition(final String condition)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addCondition(wa, condition, getState(), getScope());
				}
			});
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addCondition(wa, condition, getState(), getScope());
		}
		
		return this;
	}


	/**
	 *  Add an external condition.
	 *  @param condition The condition.
	 */
	public IEAWaitAbstraction addExternalCondition(final IExternalCondition condition)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addExternalCondition(wa, condition, getState(), getScope());
				}
			});
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addExternalCondition(wa, condition, getState(), getScope());
		}
		
		return this;
	}

	//-------- remover methods --------

	/**
	 *  Remove a message event.
	 *  @param type The type.
	 */
	public IFuture removeMessageEvent(final String type)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeMessageEvent(getState(), getScope(), type, wa);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeMessageEvent(getState(), getScope(), type, wa);
			ret.setResult(null);
		}

		return ret;
	}

	/**
	 *  Remove a message event reply.
	 *  @param me The message event.
	 */
	public IFuture removeReply(final IEAMessageEvent me)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeReply(getState(), getScope(), (ElementFlyweight)me, wa);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeReply(getState(), getScope(), (ElementFlyweight)me, wa);
			ret.setResult(null);
		}

		return ret;
	}

	/**
	 *  Remove an internal event.
	 *  @param type The type.
	 */
	public IFuture removeInternalEvent(final String type)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeInternalEvent(getState(), getScope(), type, wa);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeInternalEvent(getState(), getScope(), type, wa);
			ret.setResult(null);
		}

		return ret;
	}

	/**
	 *  Remove a goal.
	 *  @param type The type.
	 */
	public IFuture removeGoal(final String type)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeGoal(getState(), getScope(), type, wa);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeGoal(getState(), getScope(), type, wa);
			ret.setResult(null);
		}

		return ret;
	}

	/**
	 *  Remove a goal.
	 *  @param goal The goal.
	 */
	public IFuture removeGoal(final IEAGoal goal)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeGoal(getState(), (ElementFlyweight)goal, wa);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeGoal(getState(), (ElementFlyweight)goal, wa);
			ret.setResult(null);
		}

		return ret;
	}
	
	/**
	 *  Remove a fact changed.
	 *  @param belief The belief or beliefset.
	 */
	public IFuture removeFactChanged(final String belief)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeFactChanged(getState(), getScope(), belief, wa);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeFactChanged(getState(), getScope(), belief, wa);
			ret.setResult(null);
		}

		return ret;
	}
	
	/**
	 *  Remove a fact added.
	 *  @param beliefset The beliefset.
	 */
	public IFuture removeFactAdded(final String beliefset)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeFactAdded(getState(), getScope(), beliefset, wa);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeFactAdded(getState(), getScope(), beliefset, wa);
			ret.setResult(null);
		}

		return ret;
	}


	/**
	 *  Remove a fact removed.
	 *  @param beliefset The beliefset.
	 */
	public IFuture removeFactRemoved(final String beliefset)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeFactRemoved(getState(), getScope(), beliefset, wa);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeFactRemoved(getState(), getScope(), beliefset, wa);
			ret.setResult(null);
		}

		return ret;
	}
	
	/**
	 *  Remove a condition.
	 *  @param type The condition.
	 */
	public IFuture removeCondition(final String type)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeCondition(getState(), getScope(), type, wa);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeCondition(getState(), getScope(), type, wa);
			ret.setResult(null);
		}

		return ret;
	}
	
	/**
	 *  Remove an external condition.
	 *  @param condition The condition.
	 */
	public IFuture removeExternalCondition(final IExternalCondition condition)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeExternalCondition(getState(), condition, wa);
					ret.setResult(null);
				}
			});
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeExternalCondition(getState(), condition, wa);
			ret.setResult(null);
		}

		return ret;
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
