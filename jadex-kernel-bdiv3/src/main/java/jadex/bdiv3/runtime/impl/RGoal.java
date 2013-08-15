package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.actions.AdoptGoalAction;
import jadex.bdiv3.actions.DropGoalAction;
import jadex.bdiv3.actions.SelectCandidatesAction;
import jadex.bdiv3.model.MDeliberation;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MethodInfo;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.RPlan.PlanLifecycleState;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.rules.eca.Event;
import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class RGoal extends RProcessableElement implements IGoal
{
	//-------- goal lifecycle states --------
	
	public static enum GoalLifecycleState
	{
		NEW, 
		ADOPTED,
		OPTION,
		ACTIVE,
		SUSPENDED,
		DROPPING,
		DROPPED
	};
	
	public static enum GoalProcessingState
	{
		IDLE, 
		INPROCESS,
		PAUSED,
		SUCCEEDED,
		FAILED,
	};
	
	/** The lifecycle state. */
	protected GoalLifecycleState lifecyclestate;

	/** The processing state. */
	protected GoalProcessingState processingstate;

//	/** The observing rules. */
//	protected List<String> rulenames;
	
	/** The parent plan. */
	protected RPlan parentplan;
	
	/** The child plan. */
	protected RPlan childplan;
	
	/** The set of inhibitors. */
	protected Set<RGoal> inhibitors;

	/** The internal access. */
	protected IInternalAccess ia;
	
	/**
	 *  Create a new rgoal. 
	 */
	public RGoal(IInternalAccess ia, MGoal mgoal, Object goal, RPlan parentplan)
	{
		super(mgoal, goal);
		this.ia = ia;
		this.parentplan = parentplan;
		this.lifecyclestate = GoalLifecycleState.NEW;
		this.processingstate = GoalProcessingState.IDLE;
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
	 *  Get the parentplan.
	 *  @return The parentplan.
	 */
	public RPlan getParentPlan()
	{
		return parentplan;
	}

	/**
	 *  Get the lifecycleState.
	 *  @return The lifecycleState.
	 */
	public GoalLifecycleState getLifecycleState()
	{
		return lifecyclestate;
	}

	/**
	 *  Set the lifecycleState.
	 *  @param lifecycleState The lifecycleState to set.
	 */
	public void setLifecycleState(GoalLifecycleState lifecyclestate)
	{
		this.lifecyclestate = lifecyclestate;
	}

	/**
	 *  Get the processingState.
	 *  @return The processingState.
	 */
	public GoalProcessingState getProcessingState()
	{
		return processingstate;
	}

	/**
	 *  Set the processingState.
	 *  @param processingState The processingState to set.
	 */
	public void setProcessingState(GoalProcessingState processingstate)
	{
		this.processingstate = processingstate;
	}
	
	/**
	 *  Set the processingState.
	 *  @param processingState The processingState to set.
	 */
	public void setProcessingState(IInternalAccess ia, GoalProcessingState processingstate)
	{
		if(getProcessingState().equals(processingstate))
			return;
		
//		if(GoalProcessingState.FAILED.equals(processingstate))
//			System.out.println("failed: "+this);
		
		if(GoalProcessingState.FAILED.equals(getProcessingState())
			|| GoalProcessingState.SUCCEEDED.equals(getProcessingState()))
		{
			throw new RuntimeException("Final proc state cannot be changed: "+getProcessingState()+" "+processingstate);
		}
			
//		if(getId().indexOf("AnalyzeTarget")!=-1)
//			System.out.println("changeprocstate: "+this+" "+processingstate+" "+getProcessingState());
		
//		this.processingstate = processingstate;
	
		// If was inprocess -> now stop processing.
//		System.out.println("changeprocstate: "+this+" "+processingstate+" "+getProcessingState());

		if(!GoalProcessingState.INPROCESS.equals(processingstate))
		{
			// todo: introduce some state for finished?!
//			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state, null);
			setState(State.INITIAL);
			
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
		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
		
//		state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_processingstate, newstate);
		
		// If now is inprocess -> start processing
		if(GoalProcessingState.INPROCESS.equals(processingstate))
		{
//			if(getId().indexOf("AchieveCleanup")!=-1)
//				System.out.println("activating: "+this);
			ip.getRuleSystem().addEvent(new Event(ChangeEvent.GOALINPROCESS, this));
			setState(ia, RProcessableElement.State.UNPROCESSED);
		}
		else
		{
			ip.getRuleSystem().addEvent(new Event(ChangeEvent.GOALNOTINPROCESS, this));
		}
		
		if(GoalProcessingState.SUCCEEDED.equals(processingstate)
			|| GoalProcessingState.FAILED.equals(processingstate))
		{
			setLifecycleState(ia, GoalLifecycleState.DROPPING);
		}
		
//		System.out.println("exit: "+rgoal+" "+state.getAttributeValue(rgoal, OAVBDIRuntimeModel.processableelement_has_state));
	}
	
	/**
	 *  Set the lifecycle state.
	 *  @param processingState The processingState to set.
	 */
	public void setLifecycleState(IInternalAccess ia, GoalLifecycleState lifecyclestate)
	{
		if(lifecyclestate.equals(getLifecycleState()))
			return;
		
//		if(/*this.toString().indexOf("Go")!=-1 && */GoalLifecycleState.ACTIVE.equals(getLifecycleState())
//			&& GoalLifecycleState.OPTION.equals(lifecyclestate))
//		{
//			System.out.println("plan to abort: "+childplan+", "+this);
//			((BDIAgentInterpreter)((BDIAgent)ia).getInterpreter()).getCapability().dumpGoals();
//		}
		
		if(GoalLifecycleState.DROPPED.equals(getLifecycleState()))
			throw new RuntimeException("Final proc state cannot be changed: "+getLifecycleState()+" "+lifecyclestate);
		if(GoalLifecycleState.DROPPING.equals(getLifecycleState()) && !GoalLifecycleState.DROPPED.equals(lifecyclestate))
			throw new RuntimeException("Final proc state cannot be changed: "+getLifecycleState()+" "+lifecyclestate);
		
//		System.out.println("goal state change: "+this.getId()+" "+getLifecycleState()+" "+lifecyclestate);
//		if(RGoal.GOALLIFECYCLESTATE_DROPPING.equals(lifecyclestate) && RGoal.GOALLIFECYCLESTATE_NEW.equals(getLifecycleState()))
//			Thread.dumpStack();
//		if(RGoal.GOALLIFECYCLESTATE_ADOPTED.equals(lifecyclestate) && RGoal.GOALLIFECYCLESTATE_DROPPING.equals(getLifecycleState()))
//			Thread.dumpStack();
//		if(RGoal.GOALLIFECYCLESTATE_DROPPED.equals(getLifecycleState()))
//			Thread.dumpStack();
		
//		if(getId().indexOf("QueryCharging")!=-1 && GOALLIFECYCLESTATE_DROPPING.equals(lifecyclestate))
//			System.out.println("goal state change: "+this.getId()+" "+getLifecycleState()+" "+lifecyclestate);
//		if(getId().indexOf("Battery")!=-1 && GOALLIFECYCLESTATE_DROPPING.equals(lifecyclestate))
//			System.out.println("goal state change: "+this.getId()+" "+getLifecycleState()+" "+lifecyclestate);
//		if(getId().indexOf("AchieveCleanup")!=-1)
//			System.out.println("goal state change: "+this.getId()+" "+getLifecycleState()+" "+lifecyclestate);
//		if(getId().indexOf("Pick")!=-1)
//		if(ia.getExternalAccess().getComponentIdentifier().getLocalName().indexOf("Sentry")!=-1)
//			System.out.println("goal state change: "+this.getId()+" "+getLifecycleState()+" "+lifecyclestate);

		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
		setLifecycleState(lifecyclestate);
		
		if(GoalLifecycleState.ADOPTED.equals(lifecyclestate))
		{
			ip.getRuleSystem().addEvent(new Event(ChangeEvent.GOALADOPTED, this));
			setLifecycleState(ia, GoalLifecycleState.OPTION);
		}
		else if(GoalLifecycleState.ACTIVE.equals(lifecyclestate))
		{
			ip.getRuleSystem().addEvent(new Event(ChangeEvent.GOALACTIVE, this));

			// start means-end reasoning
			if(onActivate())
			{
				setProcessingState(ia, GoalProcessingState.INPROCESS);
			}
			else
			{
				setProcessingState(ia, GoalProcessingState.IDLE);
			}
		}
		else if(GoalLifecycleState.OPTION.equals(lifecyclestate))
		{
			// ready to be activated via deliberation
//			if(getId().indexOf("AchieveCleanup")!=-1)
//				System.out.println("option: "+ChangeEvent.GOALOPTION+"."+getId());
			abortPlans();
			setProcessingState(ia, GoalProcessingState.IDLE);
			ip.getRuleSystem().addEvent(new Event(ChangeEvent.GOALOPTION, this));
		}
		else if(GoalLifecycleState.SUSPENDED.equals(lifecyclestate))
		{
			// goal is suspended (no more plan executions)
//			if(getId().indexOf("PerformLook")==-1)
//				System.out.println("suspending: "+getId());
			
			abortPlans();
			setProcessingState(ia, GoalProcessingState.IDLE);
			ip.getRuleSystem().addEvent(new Event(ChangeEvent.GOALSUSPENDED, this));
		}
		
		if(GoalLifecycleState.DROPPING.equals(lifecyclestate))
		{
//			if(getId().indexOf("AchieveCleanup")!=-1)
//				System.out.println("dropping achievecleanup");
			
//			if(getId().indexOf("GetVisionAction")==-1)
//				System.out.println("dropping: "+getId());
			
//			System.out.println("dropping: "+getId());
			
			ip.getRuleSystem().addEvent(new Event(ChangeEvent.GOALDROPPED, this));
			// goal is dropping (no more plan executions)
//			setProcessingState(ia, GOALPROCESSINGSTATE_IDLE);
			abortPlans();
			ia.getExternalAccess().scheduleStep(new DropGoalAction(this));
		}
		else if(GoalLifecycleState.DROPPED.equals(lifecyclestate))
		{
			if(getListeners()!=null)
			{
				if(!isFinished())
				{
					setProcessingState(GoalProcessingState.FAILED);
					setException(new GoalFailureException());
				}
				super.notifyListeners();
			}
		}
	}
	
	/**
	 * 
	 */
	protected void abortPlans()
	{
		if(childplan!=null)
		{
			childplan.abort();
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
		return GoalProcessingState.SUCCEEDED.equals(processingstate);
	}
	
	/**
	 * 
	 */
	public boolean isFailed()
	{
		return GoalProcessingState.FAILED.equals(processingstate);
	}
	
	/**
	 * 
	 */
	public boolean isFinished()
	{
		return isSucceeded() || isFailed();
	}

//	/**
//	 *  Add a rule.
//	 */
//	protected void addRule(IRule<?> rule)
//	{
//		if(rulenames==null)
//			rulenames = new ArrayList<String>();
//		rulenames.add(rule.getName());
//	}
	
//	/**
//	 *  Unobserve a runtime goal.
//	 */
//	public void unobserveGoal(final IInternalAccess ia)
//	{
//		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
//			
//		ip.getRuleSystem().unobserveObject(getPojoElement());
//		
//		if(rulenames!=null)
//		{
//			for(String rulename: rulenames)
//			{
//				ip.getRuleSystem().getRulebase().removeRule(rulename);
//			}
//		}
//	}

	/**
	 *  Get the childplan.
	 *  @return The childplan.
	 */
	public RPlan getChildPlan()
	{
		return childplan;
	}

	/**
	 *  Set the childplan.
	 *  @param childplan The childplan to set.
	 */
	public void setChildPlan(RPlan childplan)
	{
		this.childplan = childplan;
	}
	
	/**
	 * 
	 */
	protected void addInhibitor(RGoal inhibitor, IInternalAccess ia)
	{		
		if(inhibitors==null)
			inhibitors = new HashSet<RGoal>();
		
		if(inhibitors.add(inhibitor) && inhibitors.size()==1)
		{
			BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
			ip.getRuleSystem().addEvent(new Event(ChangeEvent.GOALINHIBITED, this));
		}
		
//		if(inhibitor.getId().indexOf("AchieveCleanup")!=-1)
//			System.out.println("add inhibit: "+getId()+" "+inhibitor.getId()+" "+inhibitors);
	}
	
	/**
	 * 
	 */
	protected void removeInhibitor(RGoal inhibitor, IInternalAccess ia)
	{
//		System.out.println("rem inhibit: "+getId()+" "+inhibitor.getId()+" "+inhibitors);
		
//		if(inhibitor.getId().indexOf("AchieveCleanup")!=-1)
//			System.out.println("kokoko: "+inhibitor);
		
		if(inhibitors!=null)
		{
			if(inhibitors.remove(inhibitor) && inhibitors.size()==0)
			{
//				System.out.println("goal not inhibited: "+this);
				BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
				ip.getRuleSystem().addEvent(new Event(ChangeEvent.GOALNOTINHIBITED, this));
			}
		}
	}
	
	/**
	 * 
	 */
	protected boolean isInhibited()
	{
		return inhibitors!=null && !inhibitors.isEmpty();
	}
	
	/**
	 * 
	 */
	protected boolean isInhibitedBy(RGoal other)
	{
		return !isFinished() && inhibitors!=null && inhibitors.contains(other);
	}
	
	/**
	 *  Get the inhibitors.
	 *  @return The inhibitors.
	 */
	public Set<RGoal> getInhibitors()
	{
		return inhibitors;
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return getMGoal().isUnique()? getPojoElement().hashCode(): super.hashCode();
	}

	/**
	 *  Test if equal to other object.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof RGoal)
		{
			ret = getMGoal().isUnique()? getPojoElement().equals(((RProcessableElement)obj).getPojoElement()): super.equals(obj);
		}
		return ret;
	}

	/** 
	 * 
	 */
	public String toString()
	{
//		return "RGoal(lifecyclestate=" + lifecyclestate + ", processingstate="
//			+ processingstate + ", state=" + state + ", id=" + id + ")";
		return id+" "+getPojoElement();
	}
	
	/**
	 * 
	 */
	public void planFinished(IInternalAccess ia, RPlan rplan)
	{
		super.planFinished(ia, rplan);
		childplan = null;
		
		if(rplan!=null)
		{
			PlanLifecycleState state = rplan.getLifecycleState();
			if(state.equals(RPlan.PlanLifecycleState.FAILED))
			{
				this.setException(rplan.getException());
			}
		}
//		if(rplan!=null)
//			System.out.println("plan finished: "+rplan.getId());
		
//		if(getPojoElement().getClass().getName().indexOf("PatrolPlan")!=-1)
//			System.out.println("pips");
		
		// create reasoning step depending on the processable element type

		// Check procedural success semantics
		if(isProceduralSucceeded())
		{
			setProcessingState(ia, GoalProcessingState.SUCCEEDED);
		}
		
		if(GoalLifecycleState.ACTIVE.equals(getLifecycleState()))
		{
			if(!isSucceeded() && !isFailed())
			{
				// Test if is retry
				if(isRetry() && rplan!=null)
				{
					if(RProcessableElement.State.CANDIDATESSELECTED.equals(getState()))
					{
						if(getMGoal().getRetryDelay()>-1)
						{
							ia.getExternalAccess().scheduleStep(new SelectCandidatesAction(this), getMGoal().getRetryDelay());
						}
						else
						{
							ia.getExternalAccess().scheduleStep(new SelectCandidatesAction(this));
						}
					}
					else if(RProcessableElement.State.NOCANDIDATES.equals(getState()))
					{
						if(getException()==null)
						{
							setException(new GoalFailureException("No candidates."));
						}
						setProcessingState(ia, GoalProcessingState.FAILED);
					}
//					else
//					{
//						System.out.println("??? "+getState());
//					}
				}
				else
				{
					if(isRecur())
					{
						setProcessingState(ia, GoalProcessingState.PAUSED);
					}
					else
					{
						if(getException()==null)
						{
							setException(new GoalFailureException("No candidates."));
						}
						setProcessingState(ia, GoalProcessingState.FAILED);
					}
				}
			}
		}
	}
	
//	/**
//	 * 
//	 */
//	public void addGoalListener(IResultListener<Void> listener)
//	{
//		super.addGoalListener(listener);
//		
//		if(isSucceeded())
//		{
//			listener.resultAvailable(null);
//		}
//		else if(isFailed())
//		{
//			listener.exceptionOccurred(exception);
//		}
//		else
//		{
//			listeners.add(listener);
//		}
//	}
	
	//-------- methods that are goal specific --------

	// todo: implement those methods in goal types
	
	/**
	 * 
	 */
	public boolean onActivate()
	{
		return getMGoal().getConditions(MGoal.CONDITION_MAINTAIN)==null; // for perform, achieve, query
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
		if(isProceduralGoal() && getMGoal().isSucceedOnPassed() 
			&& getTriedPlans()!=null && !getTriedPlans().isEmpty())
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
		return !getMGoal().isDeclarative();
	}
	
	/**
	 *  Get the goal result of the pojo element.
	 *  Searches @GoalResult and delivers value.
	 */
	public static Object getGoalResult(Object pojo, MGoal mgoal, ClassLoader cl)
	{
		Object ret = pojo;
		Object pac = mgoal.getPojoResultReadAccess(cl);
		if(pac instanceof Field)
		{
			try
			{
				Field f = (Field)pac;
				f.setAccessible(true);
				ret = f.get(pojo);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(pac instanceof Method)
		{
			try
			{
				Method m = (Method)pac;
				m.setAccessible(true);
				ret = m.invoke(pojo, new Object[0]);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public void drop()
	{
		if(!GoalLifecycleState.NEW.equals(getLifecycleState())
			&& !GoalLifecycleState.DROPPING.equals(getLifecycleState()) 
			&& !GoalLifecycleState.DROPPED.equals(getLifecycleState()))
		{
			setLifecycleState(ia, GoalLifecycleState.DROPPING);
		}
	}
	
	/**
	 * 
	 */
	public void targetConditionTriggered(IInternalAccess ia, IEvent event, IRule<Void> rule, Object context)
	{
//		System.out.println("Goal target triggered: "+RGoal.this);
		if(getMGoal().getConditions(MGoal.CONDITION_MAINTAIN)!=null)
		{
			setProcessingState(ia, GoalProcessingState.IDLE);
		}
		else
		{
			setProcessingState(ia, GoalProcessingState.SUCCEEDED);
		}
	}
	
	/**
	 * 
	 * @param result
	 * @param cl
	 */
	public void setGoalResult(Object result, ClassLoader cl)
	{
		setGoalResult(result, cl, null, null, null);
	}
	
	/**
	 * 
	 */
	public void setGoalResult(Object result, ClassLoader cl, ChangeEvent event, RPlan rplan, RProcessableElement rpe)
	{
		MGoal mgoal = (MGoal)getModelElement();
		Object wa = mgoal.getPojoResultWriteAccess(cl);
		if(wa instanceof Field)
		{
			try
			{
				Field f = (Field)wa;
				f.setAccessible(true);
				f.set(getPojoElement(), result);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		else if(wa instanceof Method)
		{
			try
			{
				Method m = (Method)wa;
				BDIAgentInterpreter	bai	= ((BDIAgentInterpreter)((BDIAgent)ia).getInterpreter());
				Object[] params = bai.getInjectionValues(m.getParameterTypes(), m.getParameterAnnotations(), rplan.getModelElement(), event, rplan, rpe);
				m.invoke(getPojoElement(), params);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 *  Test if this goal inhibits the other.
	 */
	protected boolean inhibits(RGoal other, IInternalAccess ia)
	{
		if(this.equals(other))
			return false;
		
		// todo: cardinality
		
		boolean ret = false;
		
		if(getLifecycleState().equals(GoalLifecycleState.ACTIVE) && getProcessingState().equals(GoalProcessingState.INPROCESS))
		{
			MDeliberation delib = getMGoal().getDeliberation();
			if(delib!=null)
			{
				if(delib.isCardinalityOne() && other.getMGoal().equals(getMGoal()))
				{
					ret = true;
				}
				else
				{
					Set<MGoal> minh = delib.getInhibitions();
					MGoal mother = other.getMGoal();
					if(minh!=null && minh.contains(mother))
					{
						ret = true;
						
						// check if instance relation
						Map<String, MethodInfo> dms = delib.getInhibitionMethods();
						if(dms!=null)
						{
							MethodInfo mi = dms.get(mother.getName());
							if(mi!=null)
							{
								Method dm = mi.getMethod(ia.getClassLoader());
								try
								{
									dm.setAccessible(true);
									ret = ((Boolean)dm.invoke(getPojoElement(), new Object[]{other.getPojoElement()})).booleanValue();
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}
		
		return ret;
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
		public Tuple2<Boolean, Object> evaluate(IEvent event)
		{
			boolean ret = states.contains(getLifecycleState());
			if(!allowed)
				ret = !ret;
			return ret? ICondition.TRUE: ICondition.FALSE;
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
		public Tuple2<Boolean, Object> evaluate(IEvent event)
		{
			boolean ret = states.contains(getProcessingState());
			if(!allowed)
				ret = !ret;
			return ret? ICondition.TRUE: ICondition.FALSE;
		}
	}
	
//	public static void main(String[] args)
//	{
//		 PrintWriter out = new PrintWriter(System.err); 
//		List<MemoryPoolMXBean> pools = ManagementFactory.getMemoryPoolMXBeans();
//         for (MemoryPoolMXBean pool : pools) {
//           MemoryUsage peak = pool.getPeakUsage();
//           out.printf("Peak %s memory used: %,d%n", pool.getName(), peak.getUsed());
//           out.printf("Peak %s memory reserved: %,d%n", pool.getName(), peak.getCommitted());
//         }
//         out.close();
//	}
}
