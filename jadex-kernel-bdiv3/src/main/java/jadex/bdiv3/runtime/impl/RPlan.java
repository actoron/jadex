package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.actions.AdoptGoalAction;
import jadex.bdiv3.actions.ExecutePlanStepAction;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBody;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.IPlanListener;
import jadex.bdiv3.runtime.WaitAbstraction;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.ICommand;
import jadex.commons.IResultCommand;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.rules.eca.EventType;
import jadex.rules.eca.IAction;
import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.Rule;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *  Runtime element of a plan.
 */
public class RPlan extends RElement implements IPlan
{
	//-------- plan states --------
	
	public static enum PlanProcessingState
	{
		READY, 
		RUNNING,
		WAITING,
		GOALCLEANUP,
		FINISHED,
	};
	
	public static enum PlanLifecycleState
	{
		NEW, 
		BODY,
		PASSED,
		FAILED,
		ABORTED,
	};
	
	/** The plan has a reason. */
	protected Object reason;

	/** The plan has a dispatched element (current goal/event). */
	protected Object dispatchedelement;
	
	/** The plan has subgoals attribute (hack!!! redundancy to goal_has_parentplan). */
	protected List<RGoal> subgoals;
		
	/** The plan has a wait abstraction attribute. */
	protected WaitAbstraction waitabstraction;
//		
//	/** The plan has a waitqueue wait abstraction attribute. */
//	protected WaitAbstraction waitqueuewa;
	
	/** The waitqueue. */
	protected List<Object> waitqueue;
	
	/** The wait future (to resume execution). */
//	protected Future<?> waitfuture;
	protected ICommand<Void> resumecommand;
	
	/** The plan has exception attribute. */
	protected Exception exception;
	
	/** The plan has lifecycle state attribute. */
	protected PlanLifecycleState lifecyclestate;
	
	/** The plan has processing state attribute (ready or waiting). */
	protected PlanProcessingState processingstate;
	
//	/** The plan has a timer attribute (when waiting). */
//	protected static ? plan_has_timer;
	
	/** The plan body. */
	protected IPlanBody body;
	
	/** The candidate from which this plan was created. Used for tried plans in proc elem. */
	protected Object candidate;
	
	// hack?
	/** The internal access. */
	protected IInternalAccess ia;
	
	/** The plan listeners. */
	protected List<IPlanListener> listeners;
	
	/**
	 *  Create a new rplan based on an mplan.
	 */
	public static RPlan createRPlan(MPlan mplan, Object candidate, Object reason, IInternalAccess ia)
	{
		final RPlan rplan = new RPlan(mplan, candidate);
		MBody mbody = mplan.getBody();
		
		IPlanBody body = null;

		if(candidate.getClass().isAnnotationPresent(Plan.class))
		{
			body = new ClassPlanBody(ia, rplan, candidate);
		}
		else if(mbody.getClazz()!=null)
		{
			Class<?>	clazz	= (Class<?>)mbody.getClazz().getType(ia.getClassLoader());
			if(clazz.isAnnotationPresent(Plan.class))
			{
				body = new ClassPlanBody(ia, rplan, clazz);
			}
			else if(clazz.isAnnotationPresent(Agent.class))
			{
				body	= new ComponentPlanBody(clazz.getName()+".class", ia);
			}
			else
			{
				throw new RuntimeException("Neither @Plan nor @Agent annotation on plan body class: "+clazz);
			}
		}
		else if(mbody.getMethod()!=null)
		{
			Method met = mbody.getMethod().getMethod(ia.getClassLoader());
			body = new MethodPlanBody(ia, rplan, met);
		}
		else if(mbody.getServiceName()!=null)
		{
			try
			{
				IServiceParameterMapper<Object> mapper = (IServiceParameterMapper<Object>)mbody.getMapperClass().getType(ia.getClassLoader()).newInstance();
				Object plan = new ServiceCallPlan(ia, mbody.getServiceName(), mbody.getServiceMethodName(), mapper);
				body = new ClassPlanBody(ia, rplan, plan);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		else if(mbody.getComponent()!=null)
		{
			body	= new ComponentPlanBody(mbody.getComponent(), ia);
		}
		
		if(body==null)
			throw new RuntimeException("Plan body not created: "+rplan);
		
		MTrigger wqtr = mplan.getWaitqueue();
		if(wqtr!=null)
		{
			List<EventType> events = new ArrayList<EventType>();
			
			List<String> fas = wqtr.getFactAddeds();
			if(fas!=null && !fas.isEmpty())
			{
				for(String belname: fas)
				{
					events.add(new EventType(new String[]{ChangeEvent.FACTADDED, belname}));
				}
			}
			List<String> frs = wqtr.getFactRemoveds();
			if(frs!=null && !frs.isEmpty())
			{
				for(String belname: frs)
				{
					events.add(new EventType(new String[]{ChangeEvent.FACTREMOVED, belname}));
				}
			}
			List<String> fcs = wqtr.getFactChangeds();
			if(fcs!=null && !fcs.isEmpty())
			{
				for(String belname: fcs)
				{
					events.add(new EventType(new String[]{ChangeEvent.FACTCHANGED, belname}));
				}
			}
			
			if(!events.isEmpty())
			{
				final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
				final String rulename = rplan.getId()+"_waitqueue";
				Rule<Void> rule = new Rule<Void>(rulename, ICondition.TRUE_CONDITION, new IAction<Void>()
				{
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
					{
						System.out.println("Added to waitqueue: "+event);
						rplan.addToWaitqueue(new ChangeEvent(event));				
						return IFuture.DONE;
					}
				});
				rule.setEvents(events);
				ip.getRuleSystem().getRulebase().addRule(rule);
			}
		}
		
		rplan.setBody(body);
		rplan.setReason(reason);
		rplan.setDispatchedElement(reason);
		rplan.setInternalAccess(ia);
		
//		final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
//		Collection<RPlan> pls = ip.getCapability().getPlans(mplan);
//		if(pls!=null && pls.size()>0)
//		{
//			if(mplan.getName().indexOf("CleanUpWastePlan")!=-1)
//				System.out.println("doubel plan");
//			for(RPlan pl: pls)
//			{
//				if(pl.getReason()!=null && pl.getReason().equals(reason))
//					System.out.println("double plan");
//			}
//		}
		
		return rplan;
	}
	
	/**
	 * 
	 */
	public static void executePlan(RPlan rplan, IInternalAccess ia)
	{
//		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
//		ip.getCapability().addPlan(rplan);
		IConditionalComponentStep<Void> action = new ExecutePlanStepAction(rplan);
		ia.getExternalAccess().scheduleStep(action);
	}
	
	/**
	 *  Create a new plan.
	 */
	public RPlan(MPlan mplan, Object candidate)
	{
		super(mplan);
		this.candidate = candidate;
		setLifecycleState(PlanLifecycleState.NEW);
	}

	/**
	 *  Get the processingState.
	 *  @return The processingState.
	 */
	public PlanProcessingState getProcessingState()
	{
		return processingstate;
	}

	/**
	 *  Set the processingState.
	 *  @param processingState The processingState to set.
	 */
	public void setProcessingState(PlanProcessingState processingstate)
	{
		this.processingstate = processingstate;
	}

	/**
	 *  Get the lifecycleState.
	 *  @return The lifecycleState.
	 */
	public PlanLifecycleState getLifecycleState()
	{
		return lifecyclestate;
	}

	/**
	 *  Set the lifecycleState.
	 *  @param lifecycleState The lifecycleState to set.
	 */
	public void setLifecycleState(PlanLifecycleState lifecyclestate)
	{
		this.lifecyclestate = lifecyclestate;
		
		// todo: where to notify listeners
		if(listeners!=null && listeners.size()>0)
		{
			if(PlanLifecycleState.PASSED.equals(lifecyclestate)
				|| PlanLifecycleState.FAILED.equals(lifecyclestate)
				|| PlanLifecycleState.ABORTED.equals(lifecyclestate))
			{
				for(IPlanListener lis: listeners)
				{
					lis.planFinished();
				}
			}
		}
		
//		if(PlanLifecycleState.PASSED.equals(lifecyclestate)
//			|| PlanLifecycleState.FAILED.equals(lifecyclestate)
//			|| PlanLifecycleState.ABORTED.equals(lifecyclestate))
//		{
//			System.out.println("plan lifecycle: "+lifecyclestate);
//		}
	}
	
	/**
	 *  Get the reason.
	 *  @return The reason.
	 */
	public Object getReason()
	{
		return reason;
	}

	/**
	 *  Set the reason.
	 *  @param reason The reason to set.
	 */
	public void setReason(Object reason)
	{
		this.reason = reason;
	}

	/**
	 *  Get the dispatchedelement.
	 *  @return The dispatchedelement.
	 */
	public Object getDispatchedElement()
	{
		return dispatchedelement;
	}

	/**
	 *  Set the dispatchedelement.
	 *  @param dispatchedelement The dispatchedelement to set.
	 */
	public void setDispatchedElement(Object dispatchedelement)
	{
		this.dispatchedelement = dispatchedelement;
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
	 *  Get the body.
	 *  @return The body.
	 */
	public IPlanBody getBody()
	{
		return body;
	}

	/**
	 *  Set the body.
	 *  @param body The body to set.
	 */
	public void setBody(IPlanBody body)
	{
		this.body = body;
	}
	
	/**
	 *  Get the candidate.
	 *  @return The candidate.
	 */
	public Object getCandidate()
	{
		return candidate;
	}

	/**
	 *  Set the candidate.
	 *  @param candidate The candidate to set.
	 */
	public void setCandidate(Object candidate)
	{
		this.candidate = candidate;
	}
	
	/**
	 *  Get the ia.
	 *  @return The ia.
	 */
	public IInternalAccess getInternalAccess()
	{
		return ia;
	}

	/**
	 *  Set the ia.
	 *  @param ia The ia to set.
	 */
	public void setInternalAccess(IInternalAccess ia)
	{
		this.ia = ia;
	}

//	/**
//	 *  Test if the plan is waiting for a process element.
//	 */
//	public boolean isWaitingFor(Object procelem)
//	{
//		return RPlan.PLANPROCESSINGTATE_WAITING.equals(getProcessingState()) 
//			&& waitabstraction!=null && waitabstraction.isWaitingFor(procelem);
//	}
//	
	/**
	 *  Get the waitabstraction.
	 *  @return The waitabstraction.
	 */
	public WaitAbstraction getWaitabstraction()
	{
		return waitabstraction;
	}

	/**
	 *  Set the waitabstraction.
	 *  @param waitabstraction The waitabstraction to set.
	 */
	public void setWaitAbstraction(WaitAbstraction waitabstraction)
	{
		this.waitabstraction = waitabstraction;
	}

	/**
	 * 
	 */
	protected void addToWaitqueue(Object obj)
	{
		if(waitqueue==null)
			waitqueue = new ArrayList<Object>();
		waitqueue.add(obj);
	}
	
	/**
	 * 
	 */
	protected Object getFromWaitqueue()
	{
		Object ret = null;
		if(waitqueue!=null)
		{
			for(int i=0; i<waitqueue.size(); i++)
			{
				Object obj = waitqueue.get(i);
				if(waitabstraction.isWaitingFor(obj))
				{
					ret = obj;
					waitqueue.remove(i);
					break;
				}
			}
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public boolean isPassed()
	{
		return RPlan.PlanLifecycleState.PASSED.equals(lifecyclestate);
	}
	
	/**
	 * 
	 */
	public boolean isFailed()
	{
		return RPlan.PlanLifecycleState.FAILED.equals(lifecyclestate);
	}
	
	/**
	 * 
	 */
	public boolean isAborted()
	{
		return RPlan.PlanLifecycleState.ABORTED.equals(lifecyclestate);
	}
	
	/**
	 * 
	 */
	public boolean isFinished()
	{
		return isPassed() || isFailed() || isAborted();
	}
	
	/**
	 * 
	 */
	public void addSubgoal(RGoal subgoal)
	{
		if(subgoals==null)
		{
			subgoals = new ArrayList<RGoal>();
		}
		subgoals.add(subgoal);
	}
	
	/**
	 * 
	 */
	public void removeSubgoal(RGoal subgoal)
	{
		if(subgoals!=null)
		{
			subgoals.remove(subgoal);
		}
	}
	
	public boolean aborted;
	
	/**
	 * 
	 */
	public void abort()
	{
//		if(getReason() instanceof RGoal && ((RGoal)getReason()).getId().indexOf("Move")!=-1)
//			System.out.println("abort move plan: "+this);
		
		if(!isFinished())
		{
			aborted = true;
			
//			setLifecycleState(PLANLIFECYCLESTATE_ABORTED);
			setException(new PlanAbortedException()); // todo: BodyAborted
			
			if(subgoals!=null)
			{
				for(RGoal subgoal: subgoals)
				{
					subgoal.drop();
				}
			}

			// If plan is waiting interrupt waiting
			if(PlanProcessingState.WAITING.equals(getProcessingState()))
			{
				if(this.toString().indexOf("Move")!=-1)
					System.out.println("performing abort: "+this);
				RPlan.executePlan(this, ia);
			}
		}
	}
	
//	/**
//	 *  Get the waitfuture.
//	 *  @return The waitfuture.
//	 */
//	public Future<?> getWaitFuture()
//	{
//		return waitfuture;
//	}
//	
//	/**
//	 *  Get the waitfuture.
//	 */
//	public void setWaitFuture(Future<?> fut)
//	{
//		assert waitfuture==null;
//		
//		waitfuture = fut;
//	}
	
	/**
	 * 
	 */
	public void continueAfterWait()
	{
//		if(resumecommand==null)
//		{
//			System.out.println("res com null: "+resumecommand);
//				
//			first.printStackTrace();
//			
//			Thread.dumpStack();
//		}
//		else
//		{
//			first = new RuntimeException();
//		}
		
		assert resumecommand!=null;
		ICommand<Void> com = resumecommand;
		resumecommand = null;
		setProcessingState(PlanProcessingState.RUNNING);
		com.execute(null);
	}
	
	/**
	 *  Get the resumecommand.
	 *  @return The resumecommand.
	 */
	public ICommand<Void> getResumeCommand()
	{
		return resumecommand;
	}

//	Exception first = null;
	/**
	 *  Sets a resume command for continuing a plan.
	 *  Cleans dispatched element.
	 *  Sets processing state to WAITING.
	 */
	public void setResumeCommand(ICommand<Void> com)
	{
//		if(resumecommand!=null)
//		{
//			System.out.println("res com not null: "+resumecommand+" "+com);
//				
//			first.printStackTrace();
//			
//			Thread.dumpStack();
//		}
//		else
//		{
//			first = new RuntimeException();
//		}
		
		assert resumecommand==null;
		setDispatchedElement(null);
		setProcessingState(PlanProcessingState.WAITING);
		resumecommand = com;
	}
	
	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public List<Object> getWaitqueue()
	{
		return waitqueue;
	}

	/**
	 *  Set the waitqueue.
	 *  @param waitqueue The waitqueue to set.
	 */
	public void setWaitqueue(List<Object> waitqueue)
	{
		this.waitqueue = waitqueue;
	}
	
	
	// methods that can be called from pojo plan

//	/**
//	 *  Wait for a delay.
//	 */
//	public IFuture<Void> waitFor(long delay)
//	{
//		setProcessingState(PLANPROCESSINGTATE_WAITING);
//		final Future<Void> ret = new Future<Void>();
//		ia.waitForDelay(delay, new IComponentStep<Void>()
//		{
//			public IFuture<Void> execute(IInternalAccess ia)
//			{
//				if(isAborted() || isFailed())
//				{
//					return new Future<Void>(new PlanFailureException());
//				}
//				else
//				{
//					return IFuture.DONE;
//				}
//			}
//		}).addResultListener(new DelegationResultListener<Void>(ret));
//		return ret;
//	}
	
	/**
	 *  Wait for a delay.
	 */
	public IFuture<Void> waitFor(long delay)
	{
		final Future<Void> ret = new Future<Void>();
		
		final ResumeCommand<Void> rescom = new ResumeCommand<Void>(ret);
		setResumeCommand(rescom);

		ia.waitForDelay(delay, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(rescom.equals(getResumeCommand()))
				{
					RPlan.executePlan(RPlan.this, ia);
				}
				
//				if(getException()!=null)
//				{
//					return new Future<Void>(getException());
//				}
//				else
//				{
//					return IFuture.DONE;
//				}
				return IFuture.DONE;
			}
		});//.addResultListener(new DelegationResultListener<Void>(ret, true));
		
		return ret;
	}
	
	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T, E> IFuture<E> dispatchSubgoal(final T goal)
	{
		return dispatchSubgoal(goal, -1);
	}
	
	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T, E> IFuture<E> dispatchSubgoal(final T goal, long timeout)
	{
		final Future<E> ret = new Future<E>();
		
		final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();

		BDIModel bdim = ip.getBDIModel();
		final MGoal mgoal = bdim.getCapability().getGoal(goal.getClass().getName());
		if(mgoal==null)
			throw new RuntimeException("Unknown goal type: "+goal);
		final RGoal rgoal = new RGoal(ia, mgoal, goal, this);
		
		final ResumeCommand<E> rescom = new ResumeCommand<E>(ret);
		setResumeCommand(rescom);
		
		IFuture<ITimer> cont = createTimer(timeout, ip, rescom);
		cont.addResultListener(new DefaultResultListener<ITimer>()
		{
			public void resultAvailable(final ITimer timer)
			{
				if(timer!=null)
					rescom.setTimer(timer);
				
//				rgoal.addGoalListener(new TimeoutResultListener<Void>(
//					timeout, ia.getExternalAccess(), new IResultListener<Void>()
				rgoal.addGoalListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						if(rescom.equals(getResumeCommand()))
						{
							if(rgoal.isFinished() && getException()==null)
							{
								Object o = RGoal.getGoalResult(goal, mgoal, ia.getClassLoader());
								setDispatchedElement(o);
							}
							else if(getException()==null)
							{
								setException(new PlanAbortedException());
							}
								
							RPlan.executePlan(RPlan.this, ia);
							
	//						if(!rgoal.isFinished() && (isAborted() || isFailed()))
	//						{
	//							setException(new PlanFailureException());
	//						}
	//						else
	//						{
	//							Object o = RGoal.getGoalResult(goal, mgoal, ia.getClassLoader());
	//							ret.setResult((E)o);
	//						}
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						if(rescom.equals(getResumeCommand()))
						{
							setException(exception);
							RPlan.executePlan(RPlan.this, ia);
						}
					}
				});

				addSubgoal(rgoal);
				
				ip.scheduleStep(new AdoptGoalAction(rgoal));
			}
		});
		
		return ret;
	}
	
	/**
	 *  Wait for a fact change of a belief.
	 */
	public IFuture<Object> waitForFactChanged(String belname)
	{
		return waitForFactX(belname, ChangeEvent.FACTCHANGED, -1);
	}
	
	/**
	 *  Wait for a fact change of a belief.
	 */
	public IFuture<Object> waitForFactChanged(String belname , long timeout)
	{
		return waitForFactX(belname, ChangeEvent.FACTCHANGED, timeout);
	}
	
	/**
	 *  Wait for a fact being added to a belief.
	 */
	public IFuture<Object> waitForFactAdded(String belname)
	{
		return waitForFactX(belname, ChangeEvent.FACTADDED, -1);
	}
	
	/**
	 *  Wait for a fact being added to a belief.
	 */
	public IFuture<Object> waitForFactAdded(String belname, long timeout)
	{
		return waitForFactX(belname, ChangeEvent.FACTADDED, timeout);
	}

	/**
	 *  Wait for a fact being removed from a belief.
	 */
	public IFuture<Object> waitForFactRemoved(String belname)
	{
		return waitForFactX(belname, ChangeEvent.FACTREMOVED, -1);
	}
	
	/**
	 *  Wait for a fact being removed from a belief.
	 */
	public IFuture<Object> waitForFactRemoved(String belname, long timeout)
	{
		return waitForFactX(belname, ChangeEvent.FACTREMOVED, timeout);
	}
	
	/**
	 *  Wait for a fact being added to a belief..
	 */
	public IFuture<Object> waitForFactX(String belname, String evtype, long timeout)
	{
		Future<Object> ret = new Future<Object>();
		
		final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
				
		// Also set waitabstraction to know what the plan is waiting for
		final EventType et = new EventType(new String[]{evtype, belname});
		WaitAbstraction wa = new WaitAbstraction();
		wa.addChangeEventType(et.toString());
		setWaitAbstraction(wa);
		
		Object obj = getFromWaitqueue();
		if(obj!=null)
		{
			ret = new Future<Object>(obj);
		}
		else
		{
			final String rulename = getId()+"_wait";
			
			final ResumeCommand<Object> rescom = new ResumeCommand<Object>(ret, rulename);
			setResumeCommand(rescom);
			
			IFuture<ITimer> cont = createTimer(timeout, ip, rescom);
			cont.addResultListener(new DefaultResultListener<ITimer>()
			{
				public void resultAvailable(final ITimer timer)
				{
					if(timer!=null)
						rescom.setTimer(timer);
					
					Rule<Void> rule = new Rule<Void>(rulename, ICondition.TRUE_CONDITION, new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							if(rescom.equals(getResumeCommand()))
							{
								setDispatchedElement(new ChangeEvent(event));
								RPlan.executePlan(RPlan.this, ia);
							}
							return IFuture.DONE;
						}
					});
					
					rule.addEvent(et);
					ip.getRuleSystem().getRulebase().addRule(rule);
				}
			});
		}
		
		Future<Object> fut = new Future<Object>();
		ret.addResultListener(new DelegationResultListener<Object>(fut)
		{
			public void customResultAvailable(Object result)
			{
				ChangeEvent ce = (ChangeEvent)result;
				super.customResultAvailable(ce.getValue());
			}
		});
		
		return fut;
	}
	
	/**
	 *  Wait for a fact being added or removed to a belief.
	 */
	public IFuture<ChangeEvent> waitForFactAddedOrRemoved(String belname)
	{
		return waitForFactAddedOrRemoved(belname, -1);
	}
	
	/**
	 *  Wait for a fact being added or removed to a belief.
	 */
	public IFuture<ChangeEvent> waitForFactAddedOrRemoved(String belname, long timeout)
	{
		Future<ChangeEvent> ret = new Future<ChangeEvent>();
		
		// Also set waitabstraction to know what the plan is waiting for
		WaitAbstraction wa = new WaitAbstraction();
		final EventType eta = new EventType(new String[]{ChangeEvent.FACTADDED, belname});
		final EventType etb = new EventType(new String[]{ChangeEvent.FACTREMOVED, belname});
		wa.addChangeEventType(eta.toString());
		wa.addChangeEventType(etb.toString());
		setWaitAbstraction(wa);
		
		Object obj = getFromWaitqueue();
		if(obj!=null)
		{
			ret = new Future<ChangeEvent>((ChangeEvent)obj);
		}
		else
		{
			final String rulename = getId()+"_wait";
			final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
			
			final ResumeCommand<ChangeEvent> rescom = new ResumeCommand<ChangeEvent>(ret, rulename);
			setResumeCommand(rescom);
			
			IFuture<ITimer> cont = createTimer(timeout, ip, rescom);
			cont.addResultListener(new DefaultResultListener<ITimer>()
			{
				public void resultAvailable(final ITimer timer)
				{
					if(timer!=null)
						rescom.setTimer(timer);
					
					Rule<Void> rule = new Rule<Void>(rulename, ICondition.TRUE_CONDITION, new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							if(rescom.equals(getResumeCommand()))
							{
								setDispatchedElement(new ChangeEvent(event));
								RPlan.executePlan(RPlan.this, ia);
							}
							return IFuture.DONE;
						}
					});
					rule.addEvent(eta);
					rule.addEvent(etb);
					ip.getRuleSystem().getRulebase().addRule(rule);
				}
			});
		}
		
		return (Future<ChangeEvent>)ret;
	}
	
	/**
	 *  Wait for a condition.
	 */
	public IFuture<Void> waitForCondition(ICondition cond, String[] events)
	{
		return waitForCondition(cond, events, -1);
	}
	
	/**
	 *  Wait for a condition.
	 */
	public IFuture<Void> waitForCondition(final ICondition cond, final String[] events, long timeout)
	{
		Future<Object> ret = new Future<Object>();

		final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
		
		Object obj = getFromWaitqueue();
		if(obj!=null)
		{
			ret = new Future<Object>(obj);
		}
		else
		{
			final String rulename = getId()+"_wait";
			
			final ResumeCommand<Object> rescom = new ResumeCommand<Object>(ret, rulename);
			setResumeCommand(rescom);
			
			IFuture<ITimer> cont = createTimer(timeout, ip, rescom);
			cont.addResultListener(new DefaultResultListener<ITimer>()
			{
				public void resultAvailable(final ITimer timer)
				{
					if(timer!=null)
						rescom.setTimer(timer);
					
					Rule<Void> rule = new Rule<Void>(rulename, cond!=null? cond: ICondition.TRUE_CONDITION, new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
							if(rescom.equals(getResumeCommand()))
							{
								setDispatchedElement(new ChangeEvent(event));
								RPlan.executePlan(RPlan.this, ia);
							}
							return IFuture.DONE;
						}
					});
					for(String ev: events)
					{
						rule.addEvent(new EventType(ev));
					}
					ip.getRuleSystem().getRulebase().addRule(rule);
				}
			});
		}
		
		final Future<Void> fut = new Future<Void>();
		ret.addResultListener(new ExceptionDelegationResultListener<Object, Void>(fut)
		{
			public void customResultAvailable(Object result)
			{
				fut.setResult(null);
			}
		});
		return fut;
	}
	
	/**
	 * 
	 */
	protected IFuture<ITimer> createTimer(long timeout, final BDIAgentInterpreter ip, final ICommand<Void> rescom)
	{
		final Future<ITimer> ret = new Future<ITimer>();
		if(timeout>-1)
		{
			IFuture<ITimer> tfut = ((BDIAgent)ia).waitFor(timeout, new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					if(rescom.equals(getResumeCommand()))
					{
						setException(new TimeoutException());
						RPlan.executePlan(RPlan.this, ia);
					}
					return IFuture.DONE;
				}
			});
			tfut.addResultListener(new DefaultResultListener<ITimer>()
			{
				public void resultAvailable(ITimer result)
				{
					ret.setResult(result);
				}
			});
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}
	
	/**
	 * 
	 */
	public <T> IFuture<T> invokeInterruptable(IResultCommand<IFuture<T>, Void> command)
	{
		final Future<T> ret = new Future<T>();
		
		final ICommand<Void> rescom = new ResumeCommand<T>(ret, null);
		setResumeCommand(rescom);
		
		command.execute(null).addResultListener(ia.createResultListener(new IResultListener<T>()
		{
			public void resultAvailable(T result)
			{
				if(rescom.equals(getResumeCommand()))
				{
					setDispatchedElement(result);
					RPlan.executePlan(RPlan.this, ia);
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				if(rescom.equals(getResumeCommand()))
				{
					setException(exception);
					RPlan.executePlan(RPlan.this, ia);
				}
			}
		}));
		
		return ret;
	}
	
	/**
	 * 
	 */
	class ResumeCommand<T> implements ICommand<Void>
	{
		protected Future<T> waitfuture;
		protected String rulename;
		protected ITimer timer;
		
		public ResumeCommand(Future<T> waitfuture)
		{
			this(waitfuture, null);
		}
		
		public ResumeCommand(Future<T> waitfuture, String rulename)
		{
//			System.out.println("created: "+this+" "+RPlan.this.getId());
			this.waitfuture = waitfuture;
			this.rulename = rulename;
		}
		
		public void setTimer(ITimer timer)
		{
			this.timer = timer;
		}

		public void execute(Void args)
		{
//			System.out.println("exe: "+this+" "+RPlan.this.getId());
			
			if(getException()!=null)
			{
				waitfuture.setExceptionIfUndone(getException());
			}
			else
			{
				waitfuture.setResultIfUndone((T)getDispatchedElement());
			}
			
			if(rulename!=null)
			{
				BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
				ip.getRuleSystem().getRulebase().removeRule(rulename);
			}
			if(timer!=null)
			{
				timer.cancel();
			}
		}
	}
	
	/**
	 * 
	 */
	public void addPlanListener(IPlanListener listener)
	{
		if(listeners==null)
			listeners = new ArrayList<IPlanListener>();
		listeners.add(listener);
	}
	
//	/**
//	 * 
//	 */
//	class DefaultResumeCommand<T> implements ICommand<Void>
//	{
//		protected Future<T> waitfuture;
//		protected ICommand<Void> cleancom;
//		
//		public DefaultResumeCommand(Future<T> waitfuture, ICommand<Void> cleancom)
//		{
//			this.waitfuture = waitfuture;
//			this.cleancom = cleancom;
//		}
//		
//		public void execute(Void args)
//		{
//			if(getException()!=null)
//			{
//				waitfuture.setException(getException());
//			}
//			else
//			{
//				waitfuture.setResult((T)getDispatchedElement());
//			}
//			
//			if(cleancom!=null)
//				cleancom.execute(null);
//		}
//	}
}
