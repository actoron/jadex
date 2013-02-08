package jadex.bdiv3.runtime;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.actions.AdoptGoalAction;
import jadex.bdiv3.actions.ExecutePlanStepAction;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBody;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MTrigger;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.rules.eca.EventType;
import jadex.rules.eca.IAction;
import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.Rule;

import java.io.ObjectInputStream.GetField;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *  Runtime element of a plan.
 */
public class RPlan extends RElement
{
	//-------- plan states --------
	
	/** The plan ready state. */
	public static final String	PLANPROCESSINGTATE_READY	= "ready";
	
	/** The plan running state. */
	public static final String	PLANPROCESSINGTATE_RUNNING	= "running";
	
	/** The plan waiting state. */
	public static final String	PLANPROCESSINGTATE_WAITING	= "waiting";
	
	/** The plan goalcleanup state (wait for subgoals being dropped
	 *  after body is exited and before passed/failed/aborted is called). */
	public static final String	PLANPROCESSINGTATE_GOALCLEANUP	= "goalcleanup";
	
	/** The plan finished state. */
	public static final String	PLANPROCESSINGTATE_FINISHED	= "finished";
	
	/** The lifecycle state "new" (just created). */
	public static final String	PLANLIFECYCLESTATE_NEW	= "new";
	
	/** The state, indicating the execution of the plan body. */
	public static final String	PLANLIFECYCLESTATE_BODY	= "body";
	
	/** The state, indicating the execution of the passed code. */
	public static final String	PLANLIFECYCLESTATE_PASSED	= "passed";
	
	/** The state, indicating the execution of the failed code. */
	public static final String	PLANLIFECYCLESTATE_FAILED	= "failed";
	
	/** The state, indicating the execution of the aborted. */
	public static final String	PLANLIFECYCLESTATE_ABORTED	= "aborted";
	
	
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
	protected Future<?> waitfuture;
	
	/** The plan has exception attribute. */
	protected Exception exception;
	
	/** The plan has lifecycle state attribute. */
	protected String lifecyclestate;
	
	/** The plan has processing state attribute (ready or waiting). */
	protected String processingstate;
	
//	/** The plan has a timer attribute (when waiting). */
//	protected static ? plan_has_timer;
	
	/** The plan body. */
	protected IPlanBody body;
	
	/** The candidate from which this plan was created. Used for tried plans in proc elem. */
	protected Object candidate;
	
	// hack?
	/** The internal access. */
	protected IInternalAccess ia;
	
	/**
	 *  Create a new rplan based on an mplan.
	 */
	public static RPlan createRPlan(MPlan mplan, Object candidate, Object reason, IInternalAccess ia)
	{
		final RPlan rplan = new RPlan(mplan, candidate);
		MBody mbody = mplan.getBody();
		
		IPlanBody body = null;
		if(mbody.getClazz()!=null)
		{
			body = new ClassPlanBody(ia, rplan, (Class<?>)mbody.getClazz().getType(ia.getClassLoader()));
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
					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
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
		return rplan;
	}
	
	/**
	 * 
	 */
	public static void adoptPlan(RPlan rplan, IInternalAccess ia)
	{
		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
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
		setLifecycleState(PLANLIFECYCLESTATE_NEW);
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
		this.processingstate = processingstate;
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
		this.lifecyclestate = lifecyclestate;
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
		return RPlan.PLANLIFECYCLESTATE_PASSED.equals(lifecyclestate);
	}
	
	/**
	 * 
	 */
	public boolean isFailed()
	{
		return RPlan.PLANLIFECYCLESTATE_FAILED.equals(lifecyclestate);
	}
	
	/**
	 * 
	 */
	public boolean isAborted()
	{
		return RPlan.PLANLIFECYCLESTATE_ABORTED.equals(lifecyclestate);
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
	
	/**
	 * 
	 */
	public void abortPlan()
	{
		if(!isAborted() && !isFailed() && !isPassed())
		{
			setLifecycleState(PLANLIFECYCLESTATE_ABORTED);
			
			if(subgoals!=null)
			{
				for(RGoal subgoal: subgoals)
				{
					String gs = subgoal.getLifecycleState();
					
					if(!RGoal.GOALLIFECYCLESTATE_DROPPING.equals(gs) 
						&& !RGoal.GOALLIFECYCLESTATE_DROPPED.equals(gs))
					{
						subgoal.setLifecycleState(ia, RGoal.GOALLIFECYCLESTATE_DROPPING);
					}
				}
			}
			
//			BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
//			ip.getCapability().removePlan(this);
		}
	}
	
	/**
	 *  Get the waitfuture.
	 *  @return The waitfuture.
	 */
	public Future<?> getWaitFuture()
	{
		return waitfuture;
	}
	
	/**
	 *  Get the waitfuture.
	 *  @return The waitfuture.
	 */
	public void setWaitFuture(Future<?> fut)
	{
		waitfuture = fut;
	}
	
	
	// methods that can be called from pojo plan

	/**
	 *  Dispatch a goal wait for its result.
	 */
	public <T> IFuture<T> dispatchSubgoal(final T goal)
	{
		final Future<T> ret = new Future<T>();
		
		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();

		BDIModel bdim = ip.getBDIModel();
		MGoal mgoal = bdim.getCapability().getGoal(goal.getClass().getName());
		if(mgoal==null)
			throw new RuntimeException("Unknown goal type: "+goal);
		final RGoal rgoal = new RGoal(mgoal, goal, this);
		rgoal.addGoalListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				if(!rgoal.isFinished() && isAborted() || isFailed())
				{
					ret.setException(new PlanFailureException());
				}
				else
				{
					ret.setResult(goal);
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});

		addSubgoal(rgoal);
		
//		System.out.println("adopt goal");
		ip.getCapability().addGoal(rgoal);
		ip.scheduleStep(new AdoptGoalAction(rgoal));
	
		return ret;
	}
	
	/**
	 *  Wait for a delay.
	 */
	public IFuture<Void> waitFor(long delay)
	{
		setProcessingState(PLANPROCESSINGTATE_WAITING);
		final Future<Void> ret = new Future<Void>();
		ia.waitForDelay(delay, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(isAborted() || isFailed())
				{
					return new Future<Void>(new PlanFailureException());
				}
				else
				{
					return IFuture.DONE;
				}
			}
		}).addResultListener(new DelegationResultListener<Void>(ret));
		return ret;
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

	/**
	 *  Wait for a fact change of a belief.
	 */
	public IFuture<Object> waitForFactChanged(String belname)//, long delay)
	{
		return waitForFactX(belname, ChangeEvent.FACTCHANGED);
	}
	
	/**
	 *  Wait for a fact being added to a belief.
	 */
	public IFuture<Object> waitForFactAdded(String belname)//, long delay)
	{
		return waitForFactX(belname, ChangeEvent.FACTADDED);
	}

	/**
	 *  Wait for a fact being removed from a belief.
	 */
	public IFuture<Object> waitForFactRemoved(String belname)//, long delay)
	{
		return waitForFactX(belname, ChangeEvent.FACTREMOVED);
	}
	
	/**
	 *  Wait for a fact being added to a belief..
	 */
	public IFuture<Object> waitForFactX(String belname, String evtype)//, long delay)
	{
		assert getWaitFuture()==null;
		Future<Object> ret = new Future<Object>();
		
		// Also set waitabstraction to know what the plan is waiting for
		EventType et = new EventType(new String[]{evtype, belname});
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
			setWaitFuture(ret);
			setProcessingState(PLANPROCESSINGTATE_WAITING);
			
			final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
			final String rulename = getId()+"_wait";
			Rule<Void> rule = new Rule<Void>(rulename, ICondition.TRUE_CONDITION, new IAction<Void>()
			{
				public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
				{
					ip.getRuleSystem().getRulebase().removeRule(rulename);
					setDispatchedElement(new ChangeEvent(event));
					RPlan.adoptPlan(RPlan.this, ia);
					return IFuture.DONE;
				}
			});
			
			rule.addEvent(et);
			ip.getRuleSystem().getRulebase().addRule(rule);
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
	public IFuture<ChangeEvent> waitForFactAddedOrRemoved(String belname)//, long delay)
	{
		assert getWaitFuture()==null;
		Future<Object> ret = new Future<Object>();
		
		// Also set waitabstraction to know what the plan is waiting for
		WaitAbstraction wa = new WaitAbstraction();
		EventType eta = new EventType(new String[]{ChangeEvent.FACTADDED, belname});
		EventType etb = new EventType(new String[]{ChangeEvent.FACTREMOVED, belname});
		wa.addChangeEventType(eta.toString());
		wa.addChangeEventType(etb.toString());
		setWaitAbstraction(wa);
		
		Object obj = getFromWaitqueue();
		if(obj!=null)
		{
			ret = new Future<Object>(obj);
		}
		else
		{
			setWaitFuture(ret);
			setProcessingState(PLANPROCESSINGTATE_WAITING);
			
			final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
			final String rulename = getId()+"_wait";
			Rule<Void> rule = new Rule<Void>(rulename, ICondition.TRUE_CONDITION, new IAction<Void>()
			{
				public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
				{
					ip.getRuleSystem().getRulebase().removeRule(rulename);
					setDispatchedElement(new ChangeEvent(event));
					RPlan.adoptPlan(RPlan.this, ia);
					return IFuture.DONE;
				}
			});
			rule.addEvent(eta);
			rule.addEvent(etb);
			ip.getRuleSystem().getRulebase().addRule(rule);
		}
		
		return (Future<ChangeEvent>)getWaitFuture();
	}
	
	/**
	 *  Wait for a condition.
	 */
	public IFuture<Void> waitForCondition(ICondition cond, String[] events)
	{
		assert getWaitFuture()==null;
		Future<Object> ret = new Future<Object>();
		
		Object obj = getFromWaitqueue();
		if(obj!=null)
		{
			ret = new Future<Object>(obj);
		}
		else
		{
			setWaitFuture(ret);
			setProcessingState(PLANPROCESSINGTATE_WAITING);
			
			final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
			final String rulename = getId()+"_wait";
			Rule<Void> rule = new Rule<Void>(rulename, cond!=null? cond: ICondition.TRUE_CONDITION, new IAction<Void>()
			{
				public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context)
				{
					ip.getRuleSystem().getRulebase().removeRule(rulename);
					setDispatchedElement(new ChangeEvent(event));
					RPlan.adoptPlan(RPlan.this, ia);
					return IFuture.DONE;
				}
			});
			for(String ev: events)
			{
				rule.addEvent(new EventType(ev));
			}
			ip.getRuleSystem().getRulebase().addRule(rule);
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
}
