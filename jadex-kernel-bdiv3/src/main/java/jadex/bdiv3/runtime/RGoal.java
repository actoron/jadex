package jadex.bdiv3.runtime;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.actions.DropGoalAction;
import jadex.bdiv3.actions.SelectCandidatesAction;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.model.MGoal;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.rules.eca.IAction;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.MethodCondition;
import jadex.rules.eca.Rule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 
 */
public class RGoal extends RProcessableElement
{
	//-------- goal lifecycle states --------
	
	/** The lifecycle state "new" (just created). */
	public static final String	GOALLIFECYCLESTATE_NEW	= "new";

	/** The lifecycle state "adopted" (adopted, but not active). */
	public static final String	GOALLIFECYCLESTATE_ADOPTED	= "adopted";

	/** The lifecycle state "option" (adopted, but not active). */
	public static final String	GOALLIFECYCLESTATE_OPTION	= "option";

	/** The lifecycle state "active" (adopted and processed or monitored). */
	public static final String	GOALLIFECYCLESTATE_ACTIVE	= "active";

	/** The lifecycle state "active" (adopted and processed or monitored). */
	public static final String	GOALLIFECYCLESTATE_SUSPENDED	= "suspended";

	/** The lifecycle state "dropping" (just before finished, but still dropping its subgoals). */
	public static final String	GOALLIFECYCLESTATE_DROPPING	= "dropping";

	/** The lifecycle state "dropped" (goal and all subgoals finished). */
	public static final String	GOALLIFECYCLESTATE_DROPPED	= "dropped";
	
	public static Set<String> GOALLIFECYCLE_STATES = SUtil.createHashSet(new String[]
	{
		GOALLIFECYCLESTATE_NEW,
		GOALLIFECYCLESTATE_ADOPTED,
		GOALLIFECYCLESTATE_OPTION,
		GOALLIFECYCLESTATE_ACTIVE,
		GOALLIFECYCLESTATE_SUSPENDED,
		GOALLIFECYCLESTATE_DROPPING,
		GOALLIFECYCLESTATE_DROPPED
	});
	
	//-------- goal processing states --------
	
	/** The goal idle state. */
	public static final String	GOALPROCESSINGSTATE_IDLE	= "idle";
	
	/** The goal in-process state. */
	public static final String	GOALPROCESSINGSTATE_INPROCESS	= "in-process";

	/** The goal paused state. */
	public static final String	GOALPROCESSINGSTATE_PAUSED	= "paused";
	
	/** The goal succeeded state. */
	public static final String	GOALPROCESSINGSTATE_SUCCEEDED	= "succeeded";
	
	/** The goal failed state. */
	public static final String	GOALPROCESSINGSTATE_FAILED	= "failed";

	public static Set<String> GOALPROCESSINGSTATE_STATES = SUtil.createHashSet(new String[]
	{
		GOALPROCESSINGSTATE_IDLE,
		GOALPROCESSINGSTATE_INPROCESS,
		GOALPROCESSINGSTATE_PAUSED,
		GOALPROCESSINGSTATE_SUCCEEDED,
		GOALPROCESSINGSTATE_FAILED
	});
	
	/** The lifecycle state. */
	protected String lifecyclestate;

	/** The processing state. */
	protected String processingstate;

	/** The exception. */
	protected Exception exception;
	
	/** The observing rules. */
	protected List<String> rulenames;
	
	/** The goal listeners. */
	protected List<IResultListener<Void>> listeners;

	
	/**
	 *  Create a new rgoal. 
	 */
	public RGoal(MGoal mgoal, Object goal)
	{
		super(mgoal, goal);
		this.lifecyclestate = GOALLIFECYCLESTATE_NEW;
		this.processingstate = GOALPROCESSINGSTATE_IDLE;
	}
	
	/**
	 *  Get the lifecycleState.
	 *  @return The lifecycleState.
	 */
	public String getLifecycleState()
	{
		return lifecyclestate;
	}

	/**
	 *  Set the lifecycleState.
	 *  @param lifecycleState The lifecycleState to set.
	 */
	public void setLifecycleState(String lifecyclestate)
	{
		if(!GOALLIFECYCLE_STATES.contains(lifecyclestate))
			throw new IllegalArgumentException("Unknown state: "+lifecyclestate);

		this.lifecyclestate = lifecyclestate;
	}

	/**
	 *  Get the processingState.
	 *  @return The processingState.
	 */
	public String getProcessingState()
	{
		return processingstate;
	}

	/**
	 *  Set the processingState.
	 *  @param processingState The processingState to set.
	 */
	public void setProcessingState(String processingstate)
	{
		if(!GOALPROCESSINGSTATE_STATES.contains(processingstate))
			throw new IllegalArgumentException("Unknown state: "+processingstate);
		
		this.processingstate = processingstate;
	}
	
	/**
	 *  Set the processingState.
	 *  @param processingState The processingState to set.
	 */
	public void setProcessingState(IInternalAccess ia, String processingstate)
	{
//		this.processingstate = processingstate;
	
		// If was inprocess -> now stop processing.
//		Object	curstate	= state.getAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_processingstate);
//		System.out.println("changeprocstate: "+rgoal+" "+newstate+" "+curstate);

		if(!RGoal.GOALPROCESSINGSTATE_INPROCESS.equals(processingstate))
		{
			// todo: introduce some state for finished?!
//			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, null);
			setState(null);
			
			// Remove finished plans that would otherwise interfere with next goal processing (if any).
//			Collection	fplans	= state.getAttributeValues(rgoal, OAVBDIRuntimeModel.goal_has_finishedplans);
//			if(fplans!=null && !fplans.isEmpty())
//			{
//				Object[]	afplans	= fplans.toArray();
//				for(int i=0; i<afplans.length; i++)
//					state.removeAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_finishedplans, afplans[i]);
//			}
			
			// Reset event processing.
//			BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
//			if(rgoal.equals(state.getAttributeValue(ip.getAgent(), OAVBDIRuntimeModel.agent_has_eventprocessing)))
//				state.setAttributeValue(ip.getAgent(), OAVBDIRuntimeModel.agent_has_eventprocessing, null);
			
			// Reset APL.
//			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_apl, null);
			setApplicablePlanList(null);
			
			// Clean tried plans if necessary.
			setTriedPlans(null);
//			Collection coll = state.getAttributeValues(rgoal, OAVBDIRuntimeModel.goal_has_triedmplans);
//			if(coll!=null)
//			{
//				Object[]	acoll	= coll.toArray();
//				for(int i=0; i<acoll.length; i++)
//				{
//					state.removeAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_triedmplans, acoll[i]);
//				}
//			}
			
//			// Remove timers.
//			ITimer retrytimer = (ITimer)state.getAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_retrytimer);
//			if(retrytimer!=null)
//			{
//				retrytimer.cancel();
//				((InterpreterTimedObject)retrytimer.getTimedObject()).getAction().setValid(false);
//			}
			
			
//			if(!OAVBDIRuntimeModel.GOALPROCESSINGSTATE_PAUSED.equals(newstate))
//			{
//				ITimer recurtimer = (ITimer)state.getAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_recurtimer);
//				if(recurtimer!=null)
//				{
//					recurtimer.cancel();
//					((InterpreterTimedObject)recurtimer.getTimedObject()).getAction().setValid(false);
//				}
//			}
		}
		
		setProcessingState(processingstate);
//		state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_processingstate, newstate);
		
		// If now is inprocess -> start processing
		if(RGoal.GOALPROCESSINGSTATE_INPROCESS.equals(processingstate))
		{
			setState(ia, RProcessableElement.PROCESSABLEELEMENT_UNPROCESSED);
		}
		if(RGoal.GOALPROCESSINGSTATE_SUCCEEDED.equals(processingstate)
			|| RGoal.GOALPROCESSINGSTATE_FAILED.equals(processingstate))
		{
			setLifecycleState(ia, GOALLIFECYCLESTATE_DROPPING);
		}
		
//		System.out.println("exit: "+rgoal+" "+state.getAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state));
	}
	
	/**
	 *  Set the lifecycle state.
	 *  @param processingState The processingState to set.
	 */
	public void setLifecycleState(IInternalAccess ia, String lifecyclestate)
	{
		if(lifecyclestate.equals(getLifecycleState()))
			return;
		
		System.out.println("goal state change: "+this.getId()+" "+lifecyclestate);
		
		setLifecycleState(lifecyclestate);
		
		if(GOALLIFECYCLESTATE_ACTIVE.equals(lifecyclestate))
		{
			// start means-end reasoning
			setProcessingState(ia, GOALPROCESSINGSTATE_INPROCESS);
		}
		else if(GOALLIFECYCLESTATE_OPTION.equals(lifecyclestate))
		{
			// ready to be activated via deliberation
		}
		else if(GOALLIFECYCLESTATE_SUSPENDED.equals(lifecyclestate))
		{
			// goal is suspended (no more plan executions)
			setState(null);
		}
		
		if(GOALLIFECYCLESTATE_DROPPING.equals(lifecyclestate))
		{
			// goal is dropping (no more plan executions)
			setState(null);
			ia.getExternalAccess().scheduleStep(new DropGoalAction(this));
		}
		else if(GOALLIFECYCLESTATE_DROPPED.equals(lifecyclestate))
		{
			if(listeners!=null)
			{
				for(IResultListener<Void> lis: listeners)
				{
					if(isSucceeded())
					{
						lis.resultAvailable(null);
					}
					else if(isFailed())
					{
						lis.exceptionOccurred(exception);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public void planFinished(IInternalAccess ia, RPlan rplan)
	{
		super.planFinished(ia, rplan);
		// create reasoning step depending on the processable element type
		
		if(!isSucceeded() && !isFailed())
		{
			// Test if is retry
			if(getMGoal().isRetry())
			{
				if(RProcessableElement.PROCESSABLEELEMENT_CANDIDATESSELECTED.equals(getState()))
				{
					ia.getExternalAccess().scheduleStep(new SelectCandidatesAction(this));
				}
				else if(RProcessableElement.PROCESSABLEELEMENT_NOCANDIDATES.equals(getState()))
				{
					setProcessingState(ia, GOALPROCESSINGSTATE_FAILED);
				}
				else
				{
					System.out.println("??? "+getState());
				}
			}
			else
			{
				setProcessingState(ia, GOALPROCESSINGSTATE_FAILED);
			}
		}
	}
	
	/**
	 * 
	 */
	protected MGoal getMGoal()
	{
		return (MGoal)getModelElement();
	}
	
	/**
	 * 
	 */
	public boolean isSucceeded()
	{
		return RGoal.GOALPROCESSINGSTATE_SUCCEEDED.equals(processingstate);
	}
	
	/**
	 * 
	 */
	public boolean isFailed()
	{
		return RGoal.GOALPROCESSINGSTATE_FAILED.equals(processingstate);
	}
	
	/**
	 * 
	 */
	public boolean isFinished()
	{
		return isSucceeded() || isFailed();
	}
	
	/**
	 *  Get the exception.
	 *  @return The exception.
	 */
	public Exception getException()
	{
		return exception;
	}

	/**
	 *  Set the exception.
	 *  @param exception The exception to set.
	 */
	public void setException(Exception exception)
	{
		this.exception = exception;
	}

	/**
	 * 
	 */
	public void observeGoal(final IInternalAccess ia)
	{
		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
		
		// do we still need to observe directly?!
		ip.getRuleSystem().observeObject(getPojoElement());
		
		Method[] ms = getPojoElement().getClass().getDeclaredMethods();
		for(final Method m: ms)
		{
			if(m.isAnnotationPresent(GoalTargetCondition.class))
			{			
				Annotation[][] annos = m.getParameterAnnotations();
				List<String> events = new ArrayList<String>();
				for(Annotation[] ana: annos)
				{
					for(Annotation an: ana)
					{
						if(an instanceof jadex.rules.eca.annotations.Event)
						{
							events.add(((jadex.rules.eca.annotations.Event)an).value());
						}
					}
				}
				Rule<Void> rule = new Rule<Void>(getId()+"_goal_target", 
					new MethodCondition(getPojoElement(), m), new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
					{
						System.out.println("Goal target triggered: "+RGoal.this);
//						rgoal.setLifecycleState(BDIAgent.this, rgoal.GOALLIFECYCLESTATE_DROPPING);
						setProcessingState(ia, RGoal.GOALPROCESSINGSTATE_SUCCEEDED);
						
						// todo: call rgoal.finished()? succeeded or set lifecycle state directly?
						//rgoal.
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				
				ip.getRuleSystem().getRulebase().addRule(rule);
				addRule(rule);
			}
			
			if(m.isAnnotationPresent(GoalDropCondition.class))
			{			
				Annotation[][] annos = m.getParameterAnnotations();
				List<String> events = new ArrayList<String>();
				for(Annotation[] ana: annos)
				{
					for(Annotation an: ana)
					{
						if(an instanceof jadex.rules.eca.annotations.Event)
						{
							events.add(((jadex.rules.eca.annotations.Event)an).value());
						}
					}
				}
				Rule<Void> rule = new Rule<Void>(getId()+"_goal_drop", 
					new MethodCondition(getPojoElement(), m), new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
					{
						System.out.println("Goal dropping triggered: "+RGoal.this);
//						rgoal.setLifecycleState(BDIAgent.this, rgoal.GOALLIFECYCLESTATE_DROPPING);
						setException(new GoalFailureException("drop condition: "+m.getName()));
						setProcessingState(ia, RGoal.GOALPROCESSINGSTATE_FAILED);
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				
				ip.getRuleSystem().getRulebase().addRule(rule);
				addRule(rule);
			}
		}
	}
	
	/**
	 * 
	 */
	protected void addRule(IRule<?> rule)
	{
		if(rulenames==null)
			rulenames = new ArrayList<String>();
		rulenames.add(rule.getName());
	}
	
	/**
	 * 
	 */
	public void unobserveGoal(final IInternalAccess ia)
	{
		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
			
		ip.getRuleSystem().unobserveObject(getPojoElement());
		
		if(rulenames!=null)
		{
			for(String rulename: rulenames)
			{
				ip.getRuleSystem().getRulebase().removeRule(rulename);
			}
		}
	}

	/**
	 * 
	 */
	public void addGoalListener(IResultListener<Void> listener)
	{
		if(listeners==null)
			listeners = new ArrayList<IResultListener<Void>>();
		
		if(isSucceeded())
		{
			listener.resultAvailable(null);
		}
		else if(isFailed())
		{
			listener.exceptionOccurred(exception);
		}
		else
		{
			listeners.add(listener);
		}
	}
	
	/**
	 * 
	 */
	public void removeGoalListener(IResultListener<Void> listener)
	{
		if(listeners!=null)
			listeners.remove(listener);
	}
	
	/** 
	 * 
	 */
	public String toString()
	{
		return "RGoal(lifecyclestate=" + lifecyclestate + ", processingstate="
			+ processingstate + ", state=" + state + ", id=" + id + ")";
	}
	
}
