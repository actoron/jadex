package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IEAGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.GoalLifecycleRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for a goal on instance level.
 */
public class EAGoalFlyweight extends EAProcessableElementFlyweight implements IEAGoal
{
	//-------- constructors --------
	
	/**
	 *  Create a new goal flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 */
	private EAGoalFlyweight(IOAVState state, Object scope, Object handle)
	{
		super(state, scope, handle);
//		System.out.println("Created goal flyweight: "+handle+" "+scope);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static EAGoalFlyweight getGoalFlyweight(IOAVState state, Object scope, Object handle)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EAGoalFlyweight ret = (EAGoalFlyweight)ip.getFlyweightCache(IEAGoal.class).get(handle);
		if(ret==null)
		{
			ret = new EAGoalFlyweight(state, scope, handle);
			ip.getFlyweightCache(IEAGoal.class).put(handle, ret);
		}
		return ret;
	}

	//-------- BDI flags --------

	/**
	 *  Get the retry flag.
	 */
	public IFuture isRetry()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					ret.setResult(getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_retry));
				}
			});
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			ret.setResult(getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_retry));
		}
		
		return ret;
	}

	/**
	 *  Get the retry delay expression (if any).
	 *  @return The retry delay.
	 */
	public IFuture getRetryDelay()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					ret.setResult(getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_retrydelay));
				}
			});
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			ret.setResult(getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_retrydelay));
		}
		
		return ret;
	}

	/**
	 *  Get the exclude mode.
	 *  @return The exclude mode.
	 */
	public IFuture getExcludeMode()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					ret.setResult(getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_exclude));
				}
			});
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			ret.setResult(getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_exclude));
		}
		
		return ret;
	}

	/**
	 *  Get the recur flag.
	 */
	public IFuture	isRecur()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					ret.setResult(getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_recur));
				}
			});
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			ret.setResult(getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_recur));
		}
		
		return ret;
	}

	/**
	 *  Get the recur delay expression (if any).
	 */
	public IFuture getRecurDelay()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					ret.setResult(getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_recurdelay));
				}
			});
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			ret.setResult(getState().getAttributeValue(me, OAVBDIMetaModel.goal_has_recurdelay));
		}
		
		return ret;
	}

	//-------- methods --------

	/**
	 *  Get the activation state.
	 *  @return True, if the goal is active.
	 */
	public IFuture isActive()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					String state = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_lifecyclestate);
					boolean bool = OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE.equals(state);
					ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
				}
			});
		}
		else
		{
			String state = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_lifecyclestate);
			boolean bool = OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE.equals(state);
			ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
		}
		
		return ret;
	}

	/**
	 *  Check if goal is adopted
	 *  @return True, if the goal is adopted.
	 */
	public IFuture isAdopted()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					String state = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_lifecyclestate);
					boolean bool = OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ADOPTED.equals(state)
						|| OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE.equals(state) 
						|| OAVBDIRuntimeModel.GOALLIFECYCLESTATE_OPTION.equals(state) 
						|| OAVBDIRuntimeModel.GOALLIFECYCLESTATE_SUSPENDED.equals(state);
					ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
				}
			});
		}
		else
		{
			String state = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_lifecyclestate);
			boolean bool = OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ADOPTED.equals(state)
				|| OAVBDIRuntimeModel.GOALLIFECYCLESTATE_ACTIVE.equals(state) 
				|| OAVBDIRuntimeModel.GOALLIFECYCLESTATE_OPTION.equals(state) 
				|| OAVBDIRuntimeModel.GOALLIFECYCLESTATE_SUSPENDED.equals(state);
			ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
		}
		
		return ret;
	}

	/**
	 *  Get the lifecycle state.
	 *  @return The current lifecycle state (e.g. new, active, dropped).
	 */
	public IFuture getLifecycleState()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_lifecyclestate));
				}
			});
		}
		else
		{
			ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_lifecyclestate));
		}
		
		return ret;
	}

	/**
	 *  Test if a goal is finished.
	 *  @return True, if goal is finished.
	 */
	public IFuture isFinished()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					String state = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_lifecyclestate);
					boolean bool = OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED.equals(state);
					ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
				}
			});
		}
		else
		{
			String state = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_lifecyclestate);
			boolean bool = OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED.equals(state);
			ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected boolean internalIsFinished()
	{
		String state = (String)getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_lifecyclestate);
		return OAVBDIRuntimeModel.GOALLIFECYCLESTATE_DROPPED.equals(state);
	}

	/**
	 *  Test if a goal is succeeded.
	 *  This has different meanings for the different goal types.
	 *  @return True, if goal is succeeded.
	 */
	public IFuture isSucceeded()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					boolean bool;
					Object	pstate	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_processingstate);
					Object	mgoal	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					if(getState().getType(mgoal).isSubtype(OAVBDIMetaModel.maintaingoal_type))
						bool	= OAVBDIRuntimeModel.GOALPROCESSINGSTATE_IDLE.equals(pstate);
					else
						bool	= OAVBDIRuntimeModel.GOALPROCESSINGSTATE_SUCCEEDED.equals(pstate);				
					ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
				}
			});
		}
		else
		{
			boolean	bool;
			Object	pstate	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_processingstate);
			Object	mgoal	= getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			if(getState().getType(mgoal).isSubtype(OAVBDIMetaModel.maintaingoal_type))
				bool = OAVBDIRuntimeModel.GOALPROCESSINGSTATE_IDLE.equals(pstate);
			else
				bool = OAVBDIRuntimeModel.GOALPROCESSINGSTATE_SUCCEEDED.equals(pstate);	
			ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
		}
		
		return ret;
	}

	/**
	 *  Test if a goal is failed.
	 *  This has different meanings for the different goal types.
	 *  @return True, if goal has failed.
	 * /
	public boolean isFailed()
	{
		if(getInterpreter().isExternalThread())
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
	public IFuture drop()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					GoalLifecycleRules.dropGoal(getState(), getHandle());
					ret.setResult(null);
				}
			});
		}
		else
		{
			getInterpreter().startMonitorConsequences();
			GoalLifecycleRules.dropGoal(getState(), getHandle());
			getInterpreter().endMonitorConsequences();
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Get the exception (if any).
	 *  When the goal has failed, the exception can be inspected.
	 *  If more than one plan has been executed for a goal
	 *  only the last exception will be available.
	 */
	public IFuture getException()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_exception));
				}
			});
		}
		else
		{
			ret.setResult(getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.goal_has_exception));
		}
		
		return ret;
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
	public IFuture addGoalListener(final IGoalListener listener)
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
	 *  Remove a goal listener.
	 *  @param listener The goal listener.
	 */
	public IFuture removeGoalListener(final IGoalListener listener)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					// If goal is already finished, do safe removal, because listener may have been automatically removed.
					removeEventListener(listener, getHandle(), internalIsFinished());
					ret.setResult(null);
				}
			});
		}
		else
		{
			// If goal is already finished, do safe removal, because listener may have been automatically removed.
			removeEventListener(listener, getHandle(), internalIsFinished());
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
	}*/
}

