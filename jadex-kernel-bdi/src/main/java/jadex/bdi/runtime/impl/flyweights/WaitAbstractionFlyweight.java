package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MTriggerFlyweight;
import jadex.bdi.runtime.IExternalCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IWaitAbstraction;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
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
		BDIInterpreter ip = BDIAgentFeature.getInterpreter(state);
		WaitAbstractionFlyweight ret = (WaitAbstractionFlyweight)ip.getFlyweightCache(IWaitAbstraction.class, new Tuple(IWaitAbstraction.class, handle));
		if(ret==null)
		{
			ret = new WaitAbstractionFlyweight(state, scope, handle);
			ip.putFlyweightCache(IWaitAbstraction.class, new Tuple(IWaitAbstraction.class, handle), ret);
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
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addMessageEvent(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addMessageEvent(wa, type, getState(), getScope());		
			return this;
		}
	}

	/**
	 * Add a message event reply.
	 * @param me The message event.
	 */
	public IWaitAbstraction addReply(final IMessageEvent me)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addReply(wa, (ElementFlyweight)me, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addReply(wa, (ElementFlyweight)me, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add an internal event.
	 *  @param type The type.
	 */
	public IWaitAbstraction addInternalEvent(final String type)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addInternalEvent(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addInternalEvent(wa, type, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add a goal.
	 *  @param type The type.
	 */
	public IWaitAbstraction addGoal(final String type)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addGoal(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addGoal(wa, type, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add a Goal.
	 *  @param goal The goal.
	 */
	public IWaitAbstraction addGoal(final IGoal goal)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addGoal(wa, (ElementFlyweight)goal, getState(), getScope());
				}
			};
			return this;		
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addGoal(wa, (ElementFlyweight)goal, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add a fact changed.
	 */
	public IWaitAbstraction addFactChanged(final String type)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addFactChanged(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addFactChanged(wa, type, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add a fact added.
	 */
	public IWaitAbstraction addFactAdded(final String type)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addFactAdded(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addFactAdded(wa, type, getState(), getScope());
			return this;
		}
	}


	/**
	 *  Add a fact added.
	 */
	public IWaitAbstraction addFactRemoved(final String type)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addFactRemoved(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addFactRemoved(wa, type, getState(), getScope());
			return this;
		}
	}
	
	/**
	 *  Add a condition.
	 *  @param condition the condition name.
	 */
	public IWaitAbstraction addCondition(final String condition)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addCondition(wa, condition, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addCondition(wa, condition, getState(), getScope());
			return this;
		}
	}


	/**
	 *  Add an external condition.
	 *  @param condition The condition.
	 */
	public IWaitAbstraction addExternalCondition(final IExternalCondition condition)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					SFlyweightFunctionality.addExternalCondition(wa, condition, getState(), getScope());
				}
			};
			return this;		
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			SFlyweightFunctionality.addExternalCondition(wa, condition, getState(), getScope());
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
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					SFlyweightFunctionality.removeMessageEvent(getState(), getScope(), type, getWaitAbstraction());
				}
			};
		}
		else
		{
			SFlyweightFunctionality.removeMessageEvent(getState(), getScope(), type, getWaitAbstraction());
		}
	}

	/**
	 *  Remove a message event reply.
	 *  @param me The message event.
	 */
	public void removeReply(final IMessageEvent me)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					SFlyweightFunctionality.removeReply(getState(), getScope(), (ElementFlyweight)me, getWaitAbstraction());
				}
			};
		}
		else
		{
			SFlyweightFunctionality.removeReply(getState(), getScope(), (ElementFlyweight)me, getWaitAbstraction());
		}
	}

	/**
	 *  Remove an internal event.
	 *  @param type The type.
	 */
	public void removeInternalEvent(final String type)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeInternalEvent(getState(), getScope(), type, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeInternalEvent(getState(), getScope(), type, wa);
		}
	}

	/**
	 *  Remove a goal.
	 *  @param type The type.
	 */
	public void removeGoal(final String type)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeGoal(getState(), getScope(), type, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeGoal(getState(), getScope(), type, wa);
		}
	}

	/**
	 *  Remove a goal.
	 *  @param goal The goal.
	 */
	public void removeGoal(final IGoal goal)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeGoal(getState(), (ElementFlyweight)goal, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeGoal(getState(), (ElementFlyweight)goal, wa);
		}
	}
	
	/**
	 *  Remove a fact changed.
	 *  @param belief The belief or beliefset.
	 */
	public void removeFactChanged(final String belief)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeFactChanged(getState(), getScope(), belief, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeFactChanged(getState(), getScope(), belief, wa);
		}
	}
	
	/**
	 *  Remove a fact added.
	 *  @param beliefset The beliefset.
	 */
	public void removeFactAdded(final String beliefset)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeFactAdded(getState(), getScope(), beliefset, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeFactAdded(getState(), getScope(), beliefset, wa);
		}
	}


	/**
	 *  Remove a fact removed.
	 *  @param beliefset The beliefset.
	 */
	public void removeFactRemoved(final String beliefset)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeFactRemoved(getState(), getScope(), beliefset, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeFactRemoved(getState(), getScope(), beliefset, wa);
		}
	}
	
	/**
	 *  Remove a condition.
	 *  @param type The condition.
	 */
	public void removeCondition(final String type)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeCondition(getState(), getScope(), type, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeCondition(getState(), getScope(), type, wa);
		}
	}
	
	/**
	 *  Remove an external condition.
	 *  @param condition The condition.
	 */
	public void removeExternalCondition(final IExternalCondition condition)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					SFlyweightFunctionality.removeExternalCondition(getState(), condition, wa);
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			SFlyweightFunctionality.removeExternalCondition(getState(), condition, wa);
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
					object = new MTriggerFlyweight(getState(), mscope, me);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MTriggerFlyweight(getState(), mscope, me);
		}
	}
}
