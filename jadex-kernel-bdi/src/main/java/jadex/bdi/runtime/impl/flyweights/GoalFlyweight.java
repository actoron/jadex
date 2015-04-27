package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.model.IMElement;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.model.impl.flyweights.MGoalbaseFlyweight;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.GoalLifecycleRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for a goal on instance level.
 */
public class GoalFlyweight extends ProcessableElementFlyweight implements IGoal
{
	//-------- constructors --------
	
	/**
	 *  Create a new goal flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private GoalFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
//		System.out.println("Created goal flyweight: "+handle+" "+scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static GoalFlyweight getGoalFlyweight(IOAVState state, Object scope, Object handle)
	{
		IInternalBDIAgentFeature ip = BDIAgentFeature.getInterpreter(state);
		GoalFlyweight ret = (GoalFlyweight)ip.getFlyweightCache(IGoal.class, new Tuple(IGoal.class, handle));
		if(ret==null)
		{
			ret = new GoalFlyweight(state, scope, handle);
			ip.putFlyweightCache(IGoal.class, new Tuple(IGoal.class, handle), ret);
		}
		return ret;
	}

	//-------- BDI flags --------

	/**
	 *  Get the retry flag.
	 */
	public boolean	isRetry()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					bool = ((Boolean)getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_retry)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			return ((Boolean)getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_retry)).booleanValue();
		}
	}

	/**
	 *  Get the retry delay expression (if any).
	 *  @return The retry delay.
	 */
	public long	getRetryDelay()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					longint = ((Long)getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_retrydelay)).longValue();
				}
			};
			return invoc.longint;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			return ((Long)getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_retrydelay)).longValue();
		}
	}

	/**
	 *  Get the exclude mode.
	 *  @return The exclude mode.
	 */
	public String	getExcludeMode()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					string = (String)getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_exclude);
				}
			};
			return invoc.string;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			return (String)getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_exclude);
		}
	}

	/**
	 *  Get the recur flag.
	 */
	public boolean	isRecur()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					bool = ((Boolean)getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_recur)).booleanValue();
				}
			};
			return invoc.bool;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			return ((Boolean)getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_recur)).booleanValue();
		}
	}

	/**
	 *  Get the recur delay expression (if any).
	 */
	public long	getRecurDelay()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					longint = ((Long)getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_recurdelay)).longValue();
				}
			};
			return invoc.longint;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			return ((Long)getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_recurdelay)).longValue();
		}
	}

	//-------- methods --------

	/**
	 *  Get the activation state.
	 *  @return True, if the goal is active.
	 */
	public boolean	isActive()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					String state = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_lifecyclestate);
					bool = OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE.equals(state);
				}
			};
			return invoc.bool;
		}
		else
		{
			String state = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_lifecyclestate);
			return OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE.equals(state);
		}
	}

	/**
	 *  Check if goal is adopted
	 *  @return True, if the goal is adopted.
	 */
	public boolean	isAdopted()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = SFlyweightFunctionality.isAdopted(getState(), getHandle());
				}
			};
			return invoc.bool;
		}
		else
		{
			return SFlyweightFunctionality.isAdopted(getState(), getHandle());
		}
	}

	/**
	 *  Get the lifecycle state.
	 *  @return The current lifecycle state (e.g. new, active, dropped).
	 */
	public String	getLifecycleState()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					string = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_lifecyclestate);
				}
			};
			return invoc.string;
		}
		else
		{
			return (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_lifecyclestate);
		}
	}

	/**
	 *  Test if a goal is finished.
	 *  @return True, if goal is finished.
	 */
	public boolean isFinished()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = SFlyweightFunctionality.isFinished(getState(), getHandle());
				}
			};
			return invoc.bool;
		}
		else
		{
			return SFlyweightFunctionality.isFinished(getState(), getHandle());
		}
	}

	/**
	 *  Test if a goal is succeeded.
	 *  This has different meanings for the different goal types.
	 *  @return True, if goal is succeeded.
	 */
	public boolean isSucceeded()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = SFlyweightFunctionality.isSucceeded(getState(), getHandle());				
				}
			};
			return invoc.bool;
		}
		else
		{
			return SFlyweightFunctionality.isSucceeded(getState(), getHandle());				
		}
	}

	/**
	 *  Test if a goal is failed.
	 *  This has different meanings for the different goal types.
	 *  @return True, if goal has failed.
	 * /
	public boolean isFailed()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					bool = OAVBDIRuntimeModel.GOALPROCESSINGSTATE_FAILED.equals(
						getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_processingstate));
				}
			};
			return invoc.bool;
		}
		else
		{
			return OAVBDIRuntimeModel.GOALPROCESSINGSTATE_FAILED.equals(
				getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_processingstate));
		}
	}*/

	/**
	 *  Drop this goal.
	 *  Causes all associated process goals
	 *  and subgoals to be dropped.
	 */
	public void drop()
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					GoalLifecycleRules.dropGoal(getState(), getHandle());
				}
			};
		}
		else
		{
			getBDIFeature().startMonitorConsequences();
			GoalLifecycleRules.dropGoal(getState(), getHandle());
			getBDIFeature().endMonitorConsequences();
		}
	}

	/**
	 *  Get the exception (if any).
	 *  When the goal has failed, the exception can be inspected.
	 *  If more than one plan has been executed for a goal
	 *  only the last exception will be available.
	 */
	public Exception	getException()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					exception = (Exception)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_exception);
				}
			};
			return invoc.exception;
		}
		else
		{
			return (Exception)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_exception);
		}
	}
	
	//-------- parameter handling --------

	/**
	 *  Set the result for the goal.
	 *  This is a convenience method, as the goal result
	 *  is stored as property.
	 *  @param result The result.
	 *  @deprecated
	 * /
	public void	setResult(Object result)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Get the result of the goal.
	 *  This is a convenience method, as the goal result
	 *  is stored as property.
	 *  @return The result value.
	 *  @deprecated
	 * /
	public Object	getResult()
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Get the filter to wait for an info event.
	 *  @return The filter.
	 * /
	public IFilter getFilter()
	{
		throw new UnsupportedOperationException();
	}*/
	
	//-------- listeners --------
	
	/**
	 *  Add a goal listener.
	 *  @param listener The goal listener.
	 */
	public void addGoalListener(final IGoalListener listener)
	{
		if(isExternalThread())
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
	 *  Remove a goal listener.
	 *  @param listener The goal listener.
	 */
	public void removeGoalListener(final IGoalListener listener)
	{
		if(isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					// If goal is already finished, do safe removal, because listener may have been automatically removed.
					removeEventListener(listener, getHandle(), isFinished());
				}
			};
		}
		else
		{
			// If goal is already finished, do safe removal, because listener may have been automatically removed.
			removeEventListener(listener, getHandle(), isFinished());
		}
	}
	
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
					Object mgoal = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object = MGoalbaseFlyweight.createFlyweight(getState(), mscope, mgoal); 
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object mgoal = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return MGoalbaseFlyweight.createFlyweight(getState(), mscope, mgoal); 
		}
	}
}
