package jadex.bdiv3.runtime;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.actions.AdoptGoalAction;
import jadex.bdiv3.actions.DropGoalAction;
import jadex.bdiv3.actions.SelectCandidatesAction;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.rules.eca.IAction;
import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.MethodCondition;
import jadex.rules.eca.Rule;
import jadex.rules.eca.annotations.CombinedCondition;

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
	
	/** Flag if goal is declarative. */
	protected boolean declarative;
	protected boolean maintain; // hack remove me

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
	 * 
	 */
	public static void adoptGoal(RGoal rgoal, IInternalAccess ia)
	{
		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
		ip.scheduleStep(new AdoptGoalAction(rgoal));
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
			setState(PROCESSABLEELEMENT_INITIAL);
			
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
			if(onActivate())
			{
				setProcessingState(ia, GOALPROCESSINGSTATE_INPROCESS);
			}
			else
			{
				setProcessingState(ia, GOALPROCESSINGSTATE_IDLE);
			}
		}
		else if(GOALLIFECYCLESTATE_OPTION.equals(lifecyclestate))
		{
			// ready to be activated via deliberation
		}
		else if(GOALLIFECYCLESTATE_SUSPENDED.equals(lifecyclestate))
		{
			// goal is suspended (no more plan executions)
			setState(PROCESSABLEELEMENT_INITIAL);
		}
		
		if(GOALLIFECYCLESTATE_DROPPING.equals(lifecyclestate))
		{
			// goal is dropping (no more plan executions)
			setState(PROCESSABLEELEMENT_INITIAL);
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
	 *  Observe a goal by creating rules for the goal conditions.
	 */
	public void observeGoal(final IInternalAccess ia)
	{
		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
		
		// do we still need to observe directly?!
		ip.getRuleSystem().observeObject(getPojoElement());
		
		Method[] ms = getPojoElement().getClass().getDeclaredMethods();
		Method mcond = null;
		for(final Method m: ms)
		{
			if(m.isAnnotationPresent(GoalMaintainCondition.class))
				mcond = m; // do later
			
			if(m.isAnnotationPresent(GoalTargetCondition.class))
			{			
				List<String> events = readAnnotationEvents(ia, m.getParameterAnnotations());
				Rule<Void> rule = new Rule<Void>(getId()+"_goal_target", 
					new MethodCondition(getPojoElement(), m), new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
					{
						targetConditionTriggered(ia, event, rule, context);
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				ip.getRuleSystem().getRulebase().addRule(rule);
				addRule(rule);
				declarative = true;
			}
			
			if(m.isAnnotationPresent(GoalDropCondition.class))
			{			
				List<String> events = readAnnotationEvents(ia, m.getParameterAnnotations());
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
			
			if(m.isAnnotationPresent(GoalContextCondition.class))
			{			
				List<String> events = readAnnotationEvents(ia, m.getParameterAnnotations());
				Rule<Void> rule = new Rule<Void>(getId()+"_goal_suspend", 
					new CombinedCondition(new ICondition[]{
						new LifecycleStateCondition(GOALLIFECYCLESTATE_SUSPENDED, false),
						new MethodCondition(getPojoElement(), m, true),
					}), new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
					{
						System.out.println("Goal suspended: "+RGoal.this);
						setLifecycleState(GOALLIFECYCLESTATE_SUSPENDED);
						setState(PROCESSABLEELEMENT_INITIAL);
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				ip.getRuleSystem().getRulebase().addRule(rule);
				addRule(rule);
				
				rule = new Rule<Void>(getId()+"_goal_option", 
					new CombinedCondition(new ICondition[]{
						new LifecycleStateCondition(GOALLIFECYCLESTATE_OPTION, false),
						new MethodCondition(getPojoElement(), m),
					}), new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
					{
						System.out.println("Goal made option: "+RGoal.this);
//						setLifecycleState(GOALLIFECYCLESTATE_OPTION);
						setLifecycleState(GOALLIFECYCLESTATE_ACTIVE); // todo: make option and use deliberation
						setState(ia, PROCESSABLEELEMENT_INITIAL);
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				ip.getRuleSystem().getRulebase().addRule(rule);
				addRule(rule);
			}
			
			if(m.isAnnotationPresent(GoalRecurCondition.class))
			{			
				List<String> events = readAnnotationEvents(ia, m.getParameterAnnotations());
				Rule<Void> rule = new Rule<Void>(getId()+"_goal_recur", 
					new CombinedCondition(new ICondition[]{
						new LifecycleStateCondition(GOALLIFECYCLESTATE_ACTIVE),
						new ProcessingStateCondition(GOALPROCESSINGSTATE_PAUSED),
						new MethodCondition(getPojoElement(), m),
					}), new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
					{
						setTriedPlans(null);
						setApplicablePlanList(null);
						setProcessingState(ia, GOALPROCESSINGSTATE_INPROCESS);
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				ip.getRuleSystem().getRulebase().addRule(rule);
				addRule(rule);
				declarative = true;
			}
		}
		
		if(mcond!=null)
		{			
			List<String> events = readAnnotationEvents(ia, mcond.getParameterAnnotations());
			
			Rule<Void> rule = new Rule<Void>(getId()+"_goal_maintain", 
				new CombinedCondition(new ICondition[]{
					new LifecycleStateCondition(GOALLIFECYCLESTATE_ACTIVE),
					new ProcessingStateCondition(GOALPROCESSINGSTATE_IDLE),
					new MethodCondition(getPojoElement(), mcond, true),
				}), new IAction<Void>()
			{
				public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
				{
					System.out.println("Goal maintain triggered: "+RGoal.this);
//					System.out.println("state was: "+getProcessingState());
					setProcessingState(ia, GOALPROCESSINGSTATE_INPROCESS);
					return IFuture.DONE;
				}
			});
			rule.setEvents(events);
			ip.getRuleSystem().getRulebase().addRule(rule);
			addRule(rule);
			
			// if has no own target condition
			if(!declarative)
			{
				// if not has own target condition use the maintain cond
				rule = new Rule<Void>(getId()+"_goal_target", 
					new MethodCondition(getPojoElement(), mcond), new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
					{
						targetConditionTriggered(ia, event, rule, context);
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				ip.getRuleSystem().getRulebase().addRule(rule);
				addRule(rule);
			}

			declarative = true;
			maintain = true;
		}
	}
	
	/**
	 *  Read the annotation events from method annotations.
	 */
	public static List<String> readAnnotationEvents(IInternalAccess ia, Annotation[][] annos)
	{
		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
		List<String> events = new ArrayList<String>();
		for(Annotation[] ana: annos)
		{
			for(Annotation an: ana)
			{
				if(an instanceof jadex.rules.eca.annotations.Event)
				{
					String belname = ((jadex.rules.eca.annotations.Event)an).value();
					events.add(belname);
					MBelief mbel = ((MCapability)ip.getCapability().getModelElement()).getBelief(belname);
					if(mbel!=null && mbel.isMulti(ia.getClassLoader()))
					{
						events.add(ChangeEvent.FACTADDED+"."+belname);
						events.add(ChangeEvent.FACTREMOVED+"."+belname);
						events.add(ChangeEvent.FACTCHANGED+"."+belname);
					}
				}
			}
		}
		return events;
	}
	
	/**
	 *  Add a rule.
	 */
	protected void addRule(IRule<?> rule)
	{
		if(rulenames==null)
			rulenames = new ArrayList<String>();
		rulenames.add(rule.getName());
	}
	
	/**
	 *  Unobserve a runtime goal.
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
	
	/**
	 * 
	 */
	public void planFinished(IInternalAccess ia, RPlan rplan)
	{
		super.planFinished(ia, rplan);
		// create reasoning step depending on the processable element type

		// Check procedural success semantics
		if(isProceduralSucceeded())
		{
			setProcessingState(ia, RGoal.GOALPROCESSINGSTATE_SUCCEEDED);
		}
		
		if(!isSucceeded() && !isFailed())
		{
			// Test if is retry
			if(isRetry() && rplan!=null)
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
				if(isRecur())
				{
					setProcessingState(ia, GOALPROCESSINGSTATE_PAUSED);
				}
				else
				{
					setProcessingState(ia, GOALPROCESSINGSTATE_FAILED);
				}
			}
		}
	}
	
	//-------- methods that are goal specific --------

	// todo: implement those methods in goal types
	
	/**
	 * 
	 */
	public boolean onActivate()
	{
		return !maintain; // for perform, achieve, query
	}
	
	/**
	 * 
	 */
	public boolean isRetry()
	{
		return getMGoal().isRetry();
	}
	
	/**
	 * 
	 */
	public boolean isRecur()
	{
		return getMGoal().isRecur();
	}
	
	/**
	 * 
	 */
	public boolean isProceduralSucceeded()
	{
		boolean ret = false;
		
		// todo: perform goals
		if(isProceduralGoal() && !getTriedPlans().isEmpty())
		{
			RPlan rplan = getTriedPlans().get(getTriedPlans().size()-1);
			ret = rplan.isPassed();
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public boolean isProceduralGoal()
	{
		return !declarative;
	}
	
	/**
	 * 
	 */
	public void targetConditionTriggered(IInternalAccess ia, IEvent event, IRule<Void> rule, Object context)
	{
		System.out.println("Goal target triggered: "+RGoal.this);
		if(maintain)
		{
			setProcessingState(ia, RGoal.GOALPROCESSINGSTATE_IDLE);
		}
		else
		{
			setProcessingState(ia, RGoal.GOALPROCESSINGSTATE_SUCCEEDED);
		}
	}
	
	/**
	 * 
	 */
	class LifecycleStateCondition implements ICondition
	{
		/** The allowed states. */
		protected Set<String> states;
		
		/** The flag if state is allowed or disallowed. */
		protected boolean allowed;
		
		/**
		 * 
		 */
		public LifecycleStateCondition(String state)
		{
			this(SUtil.createHashSet(new String[]{state}));
		}
		
		/**
		 * 
		 */
		public LifecycleStateCondition(Set<String> states)
		{
			this(states, true);
		}
		
		/**
		 * 
		 */
		public LifecycleStateCondition(String state, boolean allowed)
		{
			this(SUtil.createHashSet(new String[]{state}), allowed);
		}
		
		/**
		 * 
		 */
		public LifecycleStateCondition(Set<String> states, boolean allowed)
		{
			this.states = states;
			this.allowed = allowed;
		}
		
		/**
		 * 
		 */
		public boolean evaluate(IEvent event)
		{
			boolean ret = states.contains(getLifecycleState());
			if(!allowed)
				ret = !ret;
			return ret;
		}
	}
	
	/**
	 * 
	 */
	class ProcessingStateCondition implements ICondition
	{
		/** The allowed states. */
		protected Set<String> states;
		
		/** The flag if state is allowed or disallowed. */
		protected boolean allowed;
		
		/**
		 * 
		 */
		public ProcessingStateCondition(String state)
		{
			this(SUtil.createHashSet(new String[]{state}));
		}
		
		/**
		 * 
		 */
		public ProcessingStateCondition(Set<String> states)
		{
			this(states, true);
		}
		
		/**
		 * 
		 */
		public ProcessingStateCondition(String state, boolean allowed)
		{
			this(SUtil.createHashSet(new String[]{state}), allowed);
		}
		
		/**
		 * 
		 */
		public ProcessingStateCondition(Set<String> states, boolean allowed)
		{
			this.states = states;
			this.allowed = allowed;
		}
		
		/**
		 * 
		 */
		public boolean evaluate(IEvent event)
		{
			boolean ret = states.contains(getProcessingState());
			if(!allowed)
				ret = !ret;
			return ret;
		}
	}
}
