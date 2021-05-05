package jadex.bdiv3.runtime.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bdiv3.actions.AdoptGoalAction;
import jadex.bdiv3.actions.ExecutePlanStepAction;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.model.MBody;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MConfigParameterElement;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MInternalEvent;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MPlanParameter;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.IGoal.GoalProcessingState;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.WaitAbstraction;
import jadex.bdiv3x.runtime.ICandidateInfo;
import jadex.bdiv3x.runtime.RInternalEvent;
import jadex.bdiv3x.runtime.RMessageEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IConditionalComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.service.component.ComponentSuspendable;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.commons.ICommand;
import jadex.commons.IFilter;
import jadex.commons.IValueFetcher;
import jadex.commons.SUtil;
import jadex.commons.TimeoutException;
import jadex.commons.Tuple2;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.javaparser.SimpleValueFetcher;
import jadex.micro.annotation.Agent;
import jadex.rules.eca.ChangeInfo;
import jadex.rules.eca.Event;
import jadex.rules.eca.EventType;
import jadex.rules.eca.IAction;
import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.Rule;

/**
 *  Runtime element of a plan.
 */
public class RPlan extends RParameterElement implements IPlan, IInternalPlan
{
	/** The rplans for plan threads. */
	public static final ThreadLocal<RPlan>	RPLANS	= new ThreadLocal<RPlan>();
	
	//-------- plan states --------
	
	public static enum PlanProcessingState
	{
		READY, 
		RUNNING,
		WAITING,
//		GOALCLEANUP,
//		FINISHED,
	};
	
	public static enum PlanLifecycleState
	{
		NEW, 
		BODY,
		
		PASSING,
		FAILING,
		ABORTING,
		
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
		
	/** The plan has a waitqueue wait abstraction attribute. */
	protected WaitAbstraction waitqueuewa;
	
	/** The waitqueue. */
	protected Waitqueue waitqueue;
	
	/** The wait future (to resume execution). */
//	protected Future<?> waitfuture;
//	protected ICommand<Boolean> resumecommand;
	
	/** The blocking resume. */
	protected ICommand<ResumeCommandArgs> resumecommand;
	
	/** The non-blocking resumes. */
	protected List<ICommand<ResumeCommandArgs>> resumecommands;
	
	/** The plan has exception attribute. */
	protected Exception exception;
	
	/** The result. */
	protected Object result;
	
	/** The plan has lifecycle state attribute. */
	protected PlanLifecycleState lifecyclestate;
	
	/** The plan has processing state attribute (ready or waiting). */
	protected PlanProcessingState processingstate;
	
//	/** The plan has a timer attribute (when waiting). */
//	protected static ? plan_has_timer;
	
	/** The plan body. */
	protected IPlanBody body;
	
	/** The candidate from which this plan was created. Used for tried plans in proc elem. */
	protected ICandidateInfo candidate;
	
//	// hack?
//	/** The internal access. */
//	protected IInternalAccess ia;
	
	/** The plan listeners. */
//	protected List<IPlanListener<?>> listeners;
	protected List<IResultListener<Object>> listeners;
	
	/** The wait cnt for rule names. */
	protected int cnt;
	
	/** The atomic flag. */
	protected boolean atomic;
	
	/** The finished future (if finishing or finished). */
	public Future<Void>	finished;
	
	/**
	 *  Create a new rplan based on an mplan.
	 *  
	 *  Reason is Object (not RProcessableElement) because it can be also ChangeEvent
	 */
	public static RPlan createRPlan(MPlan mplan, ICandidateInfo candidate, Object reason, IInternalAccess ia, Map<String, Object> binding, MConfigParameterElement config)
	{
		// Find parameter mappings for xml agents
		Map<String, Object> mappingvals = binding;
		
		// Todo: service call mappings?
		if(reason instanceof RParameterElement && mplan.getParameters()!=null && mplan.getParameters().size()>0)
		{
			RParameterElement rpe = (RParameterElement)reason;
			
			for(MParameter mparam: mplan.getParameters())
			{
				if(MParameter.Direction.IN.equals(mparam.getDirection()) || MParameter.Direction.INOUT.equals(mparam.getDirection()))
				{
					List<String> mappings = rpe instanceof RGoal ? ((MPlanParameter)mparam).getGoalMappings()
						: rpe instanceof RMessageEvent ? ((MPlanParameter)mparam).getMessageEventMappings() : ((MPlanParameter)mparam).getInternalEventMappings();
					if(mappings!=null)
					{
						for(String mapping: mappings)
						{
							MCapability	capa	= ((IBDIModel)ia.getModel()).getCapability();
							String sourceelm = mapping.substring(0, mapping.indexOf("."));
							String sourcepara = mapping.substring(mapping.indexOf(".")+1);
							
							if(rpe instanceof RGoal && capa.getGoalReferences().containsKey(sourceelm))
							{
								sourceelm	= capa.getGoalReferences().get(sourceelm);
							}
							else if((rpe instanceof RMessageEvent || rpe instanceof RInternalEvent) && capa.getEventReferences().containsKey(sourceelm))
							{
								sourceelm	= capa.getEventReferences().get(sourceelm);
							}
							
							if(rpe.getModelElement().getName().equals(sourceelm))
							{
								if(mappingvals==null)
									mappingvals = new HashMap<String, Object>();
								if(mparam.isMulti(null))
								{
									mappingvals.put(mparam.getName(), rpe.getParameterSet(sourcepara).getValues());
								}
								else
								{
									mappingvals.put(mparam.getName(), rpe.getParameter(sourcepara).getValue());
								}
								break;
							}
						}
					}
				}
			}
		}
		
		final RPlan rplan = new RPlan(mplan, candidate, reason, ia, mappingvals, config); //mappingvals==null? new RPlan(mplan, candidate, ia): 
//		rplan.setInternalAccess(ia);
		rplan.setDispatchedElement(reason);
		
		MBody mbody = mplan.getBody();
		
		IPlanBody body = null;

		if(candidate.getRawCandidate().getClass().isAnnotationPresent(Plan.class))
		{
			body = new ClassPlanBody(ia, rplan, candidate.getRawCandidate());
		}
		else if(mbody.getClazz()!=null && mbody.getServiceName()==null)
		{
			Class<?> clazz0 = (Class<?>)mbody.getClazz().getType(ia.getClassLoader());
			Class<?> clazz	= clazz0;
			while(body==null && !Object.class.equals(clazz))
			{
				if(clazz.isAnnotationPresent(Plan.class))
				{
					body = new ClassPlanBody(ia, rplan, clazz0);
				}
				else if(clazz.isAnnotationPresent(Agent.class))
				{
					body = new ComponentPlanBody(clazz0.getName()+".class", ia, rplan);
				}
				else
				{
					clazz	= clazz.getSuperclass();
				}
			}
			
			if(body==null)
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
				IServiceParameterMapper<Object> mapper;
				if(mbody.getMapperClass()!=null)
				{
					mapper = (IServiceParameterMapper<Object>)mbody.getMapperClass().getType(ia.getClassLoader()).newInstance();
				}
				else
				{
//					final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
					mapper = new DefaultAnnotationMapper(mbody.getServiceName(), ia);
				}
				Object plan = new ServiceCallPlan(ia, mbody.getServiceName(), mbody.getServiceMethodName(), mapper, rplan);
				body = new ClassPlanBody(ia, rplan, plan);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		else if(mbody.getComponent()!=null)
		{
			body	= new ComponentPlanBody(mbody.getComponent(), ia, rplan);
		}
		
		if(body==null)
			throw new RuntimeException("Plan body not created: "+rplan);
		
		MTrigger wqtr = mplan.getWaitqueue();
		if(wqtr!=null)
		{
			List<EventType> events = new ArrayList<EventType>();
			
			for(String belname: SUtil.notNull(wqtr.getFactAddeds()))
			{
				events.add(new EventType(new String[]{ChangeEvent.FACTADDED, belname}));
			}
			for(String belname: SUtil.notNull(wqtr.getFactRemoveds()))
			{
				events.add(new EventType(new String[]{ChangeEvent.FACTREMOVED, belname}));
			}
			for(String belname: SUtil.notNull(wqtr.getFactChangeds()))
			{
				events.add(new EventType(new String[]{ChangeEvent.FACTCHANGED, belname}));
			}			
			for(MGoal goal: SUtil.notNull(wqtr.getGoalFinisheds()))
			{
				events.add(new EventType(new String[]{ChangeEvent.GOALDROPPED, goal.getName()}));
			}
			
			if(!events.isEmpty())
			{
////			final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
//				final String rulename = rplan.getId()+"_waitqueue";
//				Rule<Void> rule = new Rule<Void>(rulename, ICondition.TRUE_CONDITION, new IAction<Void>()
//				{
//					public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
//					{
//						System.out.println("Added to waitqueue: "+event);
//						rplan.addToWaitqueue(new ChangeEvent(event));				
//						return IFuture.DONE;
//					}
//				});
//				rule.setEvents(events);
//				ia.getComponentFeature(IInternalBDIAgentFeature.class).getRuleSystem().getRulebase().addRule(rule);
				
				rplan.internalSetupEventsRule(events);
			}
			
			for(MInternalEvent mevent: SUtil.notNull(wqtr.getInternalEvents()))
			{
				WaitAbstraction wa = rplan.getOrCreateWaitqueueWaitAbstraction();
				wa.addModelElement(mevent);
			}
			for(MMessageEvent mevent: SUtil.notNull(wqtr.getMessageEvents()))
			{
				WaitAbstraction wa = rplan.getOrCreateWaitqueueWaitAbstraction();
				wa.addModelElement(mevent);
			}
			
			// Todo: not for waitqueue, like goals...?
//			for(MServiceCall mevent: SUtil.safeList(wqtr.getServices()))
//			{
//				WaitAbstraction wa = rplan.getOrCreateWaitqueueWaitAbstraction();
//				wa.addModelElement(mevent);
//			}
		}
		
		rplan.setBody(body);
		
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
	 *  Add reason to fetcher.
	 */
	@Override
	public SimpleValueFetcher wrapFetcher(IValueFetcher fetcher)
	{
		SimpleValueFetcher	ret	= super.wrapFetcher(fetcher);
		if(reason instanceof RParameterElement)
		{
			ret.setValue(((RParameterElement)reason).getFetcherName(), reason);
		}
		return ret;
	}
	
	/**
	 *  Get the pojo plan of a plan.
	 *  @return The pojo plan.
	 */
	public Object getPojoPlan()
	{
		return body instanceof ClassPlanBody? ((ClassPlanBody)body).getPojoPlan(): null;
	}
	
	/**
	 *  Execute a plan.
	 */
	public static IFuture<Object> executePlan(RPlan rplan, IInternalAccess ia)
	{
		Future<Object> ret = new Future<Object>();
		
		if(rplan.toString().indexOf("cnp_make_proposal")!=-1)
			System.out.println("execute plan: "+rplan);
		
//		executePlan(rplan, ia, null);
		IConditionalComponentStep<Void> action = new ExecutePlanStepAction(rplan);
		ia.getFeature(IExecutionFeature.class).scheduleStep(action);
		
		rplan.addListener(new DelegationResultListener<Object>(ret));
		
		if(rplan.toString().indexOf("cnp_make_proposal")!=-1)
			System.out.println("scheduled plan: "+rplan);
		
		return ret;
	}
	
//	/**
//	 *  Execute a plan.
//	 */
//	public static void executePlan(RPlan rplan, IInternalAccess ia, ICommand<Boolean> resume)
//	{
////		BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
////		ip.getCapability().addPlan(rplan);
//		IConditionalComponentStep<Void> action = new ExecutePlanStepAction(rplan, resume);
//		ia.getExternalAccess().scheduleStep(action);
//	}
	
//	/**
//	 *  Create a new plan.
//	 */
//	public RPlan(MPlan mplan, Object candidate, IInternalAccess agent)
//	{
//		this(mplan, candidate, agent, null);
//	}
	
	/**
	 *  Create a new plan.
	 */
	public RPlan(MPlan mplan, ICandidateInfo candidate, Object reason, IInternalAccess agent, Map<String, Object> mappingvals, MConfigParameterElement config)
	{
		super(mplan, agent, mappingvals, config);
		this.candidate = candidate;
		this.reason = reason;
		setLifecycleState(PlanLifecycleState.NEW);
		setProcessingState(PlanProcessingState.READY);
		
		// Tricky, requires reason to be set before initing parameters.
		super.initParameters(mappingvals, config);
	}
	
	/**
	 *  Create the parameters from model spec.
	 */
	@Override
	public void initParameters(Map<String, Object> vals, MConfigParameterElement config)
	{
		// do nothing in super constructor init 
	}
	
	/**
	 *  Get the name of the element in the fetcher (e.g. $goal).
	 *  @return The element name in the fetcher name.
	 */
	public String getFetcherName()
	{
		return "$plan";
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
//	public Exception e = null;
	public void setProcessingState(PlanProcessingState processingstate)
	{
//		if(getId().indexOf("Move")!=-1)
//		{
//			System.out.println("proc state: "+getId()+" "+processingstate);
//			Thread.dumpStack();
//		}
//		if(processingstate.equals(PlanProcessingState.RUNNING))
//		{
//			System.out.println("proc state: "+getId()+" "+processingstate);
////			e = new RuntimeException();
//		}
		this.processingstate = processingstate;
		
		publishToolPlanEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION);
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
//		if(lifecyclestate.equals(PlanLifecycleState.ABORTED))
//			System.out.println("state: "+this+", "+lifecyclestate);
		
		// Cleanup previous lifecycle phase
		if(subgoals!=null)
		{
			for(IGoal subgoal: subgoals)
			{
				// Todo: wait for goals dropped?
				subgoal.drop();
			}
		}
		
		this.lifecyclestate = lifecyclestate;
		
		if(PlanLifecycleState.BODY.equals(lifecyclestate))
		{
			getRuleSystem().addEvent(new Event(new EventType(new String[]{ChangeEvent.PLANADOPTED, getModelElement().getName()}), this));
			publishToolPlanEvent(IMonitoringEvent.EVENT_TYPE_CREATION);
		}
		else if(PlanLifecycleState.PASSED.equals(lifecyclestate) 
			|| PlanLifecycleState.FAILED.equals(lifecyclestate) 
			|| PlanLifecycleState.ABORTED.equals(lifecyclestate))
		{
			getRuleSystem().addEvent(new Event(new EventType(new String[]{ChangeEvent.PLANFINISHED, getModelElement().getName()}), this));
			publishToolPlanEvent(IMonitoringEvent.EVENT_TYPE_DISPOSAL);
		}
		else
		{
			publishToolPlanEvent(IMonitoringEvent.EVENT_TYPE_MODIFICATION);
		}
		
		if(PlanLifecycleState.PASSED.equals(lifecyclestate)
			|| PlanLifecycleState.FAILED.equals(lifecyclestate)
			|| PlanLifecycleState.ABORTED.equals(lifecyclestate))
		{
			assert finished!=null;
			if(finished!=null)
			{
				finished.setResult(null);
			}
			
			// todo: where to notify listeners
			notifyListeners();
//			if(listeners!=null && listeners.size()>0)
//			{
//				for(IPlanListener<?> lis: listeners)
//				{
//					((IPlanListener)lis).planFinished(getResult());
//				}
//			}
		}
		
//		if(PlanLifecycleState.PASSED.equals(lifecyclestate)
//			|| PlanLifecycleState.FAILED.equals(lifecyclestate)
//			|| PlanLifecycleState.ABORTED.equals(lifecyclestate))
//		{
//			System.out.println("plan lifecycle: "+lifecyclestate+", "+this);
//		}
	}
	
	/**
	 *  Notify the listeners.
	 */
	public void notifyListeners()
	{
		if(getListeners()!=null)
		{
			for(IResultListener<Object> lis: getListeners())
			{
				if(isSucceeded())
				{
					lis.resultAvailable(getResult());
				}
				else if(isFailed())
				{
					lis.exceptionOccurred(exception);
				}
			}
		}
	}
	
	/**
	 *  Add a new listener to get notified when the goal is finished.
	 *  @param listener The listener.
	 */
	public void addListener(IResultListener<Object> listener)
	{
		if(listeners==null)
			listeners = new ArrayList<IResultListener<Object>>();
		
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
	 *  Remove a listener.
	 */
	public void removeListener(IResultListener<Object> listener)
	{
		if(listeners!=null)
			listeners.remove(listener);
	}
	
	/**
	 *  Get the listeners.
	 *  @return The listeners.
	 */
	public List<IResultListener<Object>> getListeners()
	{
		return listeners;
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
//		System.out.println("setting ex: "+exception+" "+this);
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
	public ICandidateInfo getCandidate()
	{
		return candidate;
	}

	/**
	 *  Set the candidate.
	 *  @param candidate The candidate to set.
	 */
	public void setCandidate(ICandidateInfo candidate)
	{
		this.candidate = candidate;
	}
	
//	/**
//	 *  Get the ia.
//	 *  @return The ia.
//	 */
//	public IInternalAccess getInternalAccess()
//	{
//		return ia;
//	}

//	/**
//	 *  Set the ia.
//	 *  @param ia The ia to set.
//	 */
//	public void setInternalAccess(IInternalAccess ia)
//	{
//		this.ia = ia;
//	}

	/**
	 *  Test if the plan is waiting for a process element.
	 */
	public boolean isWaitingFor(Object procelem)
	{
		return RPlan.PlanProcessingState.WAITING.equals(getProcessingState()) 
			&& waitabstraction!=null && waitabstraction.isWaitingFor(procelem);
	}
	
	/**
	 *  Get the waitabstraction.
	 *  @return The waitabstraction.
	 */
	public WaitAbstraction getWaitAbstraction()
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
	 *  Test if the plan is always waiting for a process element (waitqueue wait).
	 */
	public boolean isWaitqueueWaitingFor(Object procelem)
	{
		// Do not dispatch process goals to waitqueue. (Hack? not allowed for v2, but would be easily possible for v3)
		boolean	processgoal	= procelem instanceof RGoal && ((RGoal)procelem).getProcessingState()==GoalProcessingState.INPROCESS;
		
		return !processgoal && waitqueuewa!=null && waitqueuewa.isWaitingFor(procelem);
	}
	
//	/**
//	 *  Get the waitabstraction.
//	 *  @return The waitabstraction.
//	 */
//	public WaitAbstraction getWaitqueueWaitAbstraction()
//	{
//		return waitqueuewa;
//	}
	
	/**
	 *  Get the waitabstraction.
	 *  @return The waitabstraction.
	 */
	public WaitAbstraction getOrCreateWaitqueueWaitAbstraction()
	{
		if(waitqueuewa==null)
			waitqueuewa = new WaitAbstraction();
		return waitqueuewa;
	}
	
//	/**
//	 *  Set the waitabstraction.
//	 *  @param waitabstraction The waitabstraction to set.
//	 */
//	public void setWaitqueueWaitAbstraction(WaitAbstraction waitabstraction)
//	{
//		this.waitqueuewa = waitabstraction;
//	}

	/**
	 * 
	 */
	protected void addToWaitqueue(Object obj)
	{
		if(waitqueue==null)
			waitqueue = new Waitqueue();
		waitqueue.addElement(obj);
	}
	
	/**
	 * 
	 */
	public Object getFromWaitqueue(WaitAbstraction wa)
	{
		return waitqueue!=null ? waitqueue.getFromWaitqueue(wa) : null;
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
	 *  Start the finishing of the plan.
	 */
	public void setFinishing()
	{
		assert finished==null;
		assert getAgent().getFeature(IExecutionFeature.class).isComponentThread();
		finished	= new Future<Void>();
	}
	
	/**
	 *  Test, if the plan end state (passed/failed/aborted) is started or done. 
	 */
	public boolean isFinishing()
	{
		return finished!=null;
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
	
	/**
	 * 
	 */
	public IFuture<Void> abort()
	{
		//if(agent.getId().toString().indexOf("Sokrates")!=-1)
		//System.out.println("aborting: "+this+" "+IComponentIdentifier.LOCAL.get()+" "+agent.getId());
		
		if(!isFinishing())
		{
			setFinishing();
			
			if(!isFinished())
			{
	//			setLifecycleState(PLANLIFECYCLESTATE_ABORTED);
				Exception ex = new PlanAbortedException();
				setException(ex); // remove? // todo: BodyAborted
				
				// Stop plan execution if any.
//				System.out.println("aborting2: "+this);
				body.abort();
//				System.out.println("aborting3: "+this);
				
				// If plan is waiting interrupt waiting
				if(PlanProcessingState.WAITING.equals(getProcessingState()))
				{
//					System.out.println("aborting4: "+this);
	//				RPlan.executePlan(this, ia, new ICommand<Boolean>()
	//				{
	//					public void execute(Boolean args)
	//					{
							// The resume command continues the blocked plan thread and
							// the commands are to continue all listeners on hold
							// This is not completely clean because the agent does not wait for these threads
					
							ICommand<ResumeCommandArgs> resc = getResumeCommand();
							if(resc!=null)
							{
								//System.out.println("aborting5: "+this+", "+resc);
								resc.execute(new ResumeCommandArgs(null, null, ex));
							}
							List<ICommand<ResumeCommandArgs>> rescoms = getResumeCommands();
							if(rescoms!=null)
							{
								ICommand<ResumeCommandArgs>[] tmp = (ICommand<ResumeCommandArgs>[])rescoms.toArray(new ICommand[rescoms.size()]);
								//System.out.println("aborting6: "+this+", "+SUtil.arrayToString(tmp));
								for(ICommand<ResumeCommandArgs> rescom: tmp)
								{
									rescom.execute(new ResumeCommandArgs(null, null, ex));
								}
							}
	//					}
	//				});
				}
				else
				{
					// happens with state=RUNNING ?!
					//System.out.println("plan abort: not performing abort due to plan state: "+getProcessingState());
				}
	//			// Can be currently executing and being abort due to e.g. goal condition triggering
	//			else if(PlanProcessingState.RUNNING.equals(getProcessingState()))
	//			{
					// it will detect the abort in beforeBlock() when next future.get() is
					// called and will avoid the next wait
	//			}
	//			else if(!PlanLifecycleState.NEW.equals(getLifecycleState()))
	//			{
	//				System.out.println("Cannot abort plan: "+getId()+" "+getProcessingState()+" "+getLifecycleState());
	//			}
			}			
		}
		
		return finished;
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
//	public void continueAfterWait(ICommand<Boolean> resumecommand)
//	{
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
		
//		assert resumecommand!=null;
//		ICommand<Boolean> com = resumecommand;
//		resumecommand = null;
//		setProcessingState(PlanProcessingState.RUNNING);
//		resumecommand.execute(null);
//	}
	
//	/**
//	 *  Get the resumecommand.
//	 *  @return The resumecommand.
//	 */
//	public ICommand<Boolean> getResumeCommand()
//	{
//		return resumecommand;
//	}

//	Exception first = null;
//	/**
//	 *  Sets a resume command for continuing a plan.
//	 *  Cleans dispatched element.
//	 *  Sets processing state to WAITING.
//	 */
//	public void setResumeCommand(ICommand<Boolean> com)
//	{
////		if(resumecommand!=null)
////		{
////			System.out.println("res com not null: "+resumecommand+" "+com);
////				
////			first.printStackTrace();
////			
////			Thread.dumpStack();
////		}
////		else
////		{
////			first = new RuntimeException();
////		}
//		
//		assert resumecommand==null;
//		setDispatchedElement(null);
//		setProcessingState(PlanProcessingState.WAITING);
//		resumecommand = com;
//	}
	
	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public Waitqueue getWaitqueue()
	{
		if(waitqueue==null)
		{
			waitqueue = new Waitqueue();
		}
		return waitqueue;
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
		//System.out.println("before wait: "+delay+" "+agent.getId());
		
		final Future<Void> ret = new BDIFuture<Void>();
		
		final ResumeCommand<Void> rescom = new ResumeCommand<Void>(ret, true);
//		setResumeCommand(rescom);
		addResumeCommand(rescom);

		getAgent().getFeature(IExecutionFeature.class).waitForDelay(delay, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				//System.out.println("after wait: "+delay+" "+agent.getId());
//				if(rescom.equals(getResumeCommand()))
				{
					rescom.execute(null);
//					RPlan.executePlan(RPlan.this, ia, rescom);
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
		}, false);//.addResultListener(new DelegationResultListener<Void>(ret, true));
		
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
//		final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();

		final Future<E> ret = new BDIFuture<E>();
		
		IBDIModel bdim = getAgent().getFeature(IInternalBDIAgentFeature.class).getBDIModel();
		final MGoal mgoal = bdim.getCapability().getGoal(goal.getClass().getName());
		if(mgoal==null)
			throw new RuntimeException("Unknown goal type: "+goal);
		final RGoal rgoal = new RGoal(getAgent(), mgoal, goal, null, null, null, null);
		rgoal.setParent(this);
		
		final ResumeCommand<E> rescom = new ResumeCommand<E>(ret, false);
//		setResumeCommand(rescom);
		addResumeCommand(rescom);
		
		final	PlanLifecycleState lcs	= getLifecycleState();
		
		IFuture<ITimer> cont = createTimer(timeout, getAgent(), rescom);
		cont.addResultListener(new DefaultResultListener<ITimer>()
		{
			public void resultAvailable(final ITimer timer)
			{
				if(lcs.equals(getLifecycleState()))
				{
					if(timer!=null)
						rescom.setTimer(timer);
					
	//				rgoal.addGoalListener(new TimeoutResultListener<Void>(
	//					timeout, getAgent().getExternalAccess(), new IResultListener<Void>()
					rgoal.addListener(new IResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
							if(getException()==null)
							{
								Object o = RGoal.getGoalResult(rgoal, getAgent().getClassLoader());
								if(o==null)
									o = goal;
								setDispatchedElement(o);
								
								// Non-maintain goal -> remove subgoal.
								if(rgoal.isFinished())
								{
									removeSubgoal(rgoal);
								}
								
								// else keep maintain goal until plan is finished
								// todo: allow explicit removal / redispatch
							}
							
							rescom.execute(null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
//							if(rescom.equals(getResumeCommand()))
							{
//								setException(exception);
//								RPlan.executePlan(RPlan.this, getAgent(), rescom);
								rescom.execute(new ResumeCommandArgs(null, null, exception));
								removeSubgoal(rgoal);
							}
						}
					});
	
					addSubgoal(rgoal);
					
					AdoptGoalAction.adoptGoal(getAgent(), rgoal);
//					getAgent().getComponentFeature(IExecutionFeature.class).scheduleStep(new AdoptGoalAction(rgoal))
//						.addResultListener(new IResultListener<Void>()
//					{
//						public void resultAvailable(Void result)
//						{
//						}
//						public void exceptionOccurred(Exception exception)
//						{
//						}
//					});
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Wait for a fact change of a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactChanged(String belname)
	{
		return waitForFactX(belname, new String[]{ChangeEvent.FACTCHANGED}, -1, null);
	}
	
	/**
	 *  Wait for a fact change of a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactChanged(String belname , long timeout)
	{
		return waitForFactX(belname, new String[]{ChangeEvent.FACTCHANGED}, timeout, null);
	}
	
	/**
	 *  Wait for a fact being added to a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactAdded(String belname)
	{
		return waitForFactX(belname, new String[]{ChangeEvent.FACTADDED}, -1, null);
	}
	
	/**
	 *  Wait for a fact being added to a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactAdded(String belname, long timeout)
	{
		return waitForFactX(belname, new String[]{ChangeEvent.FACTADDED}, timeout, null);
	}

	/**
	 *  Wait for a fact being removed from a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactRemoved(String belname)
	{
		return waitForFactX(belname, new String[]{ChangeEvent.FACTREMOVED}, -1, null);
	}
	
	/**
	 *  Wait for a fact being removed from a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactRemoved(String belname, long timeout)
	{
		return waitForFactX(belname, new String[]{ChangeEvent.FACTREMOVED}, timeout, null);
	}
	
	/**
	 *  Wait for a fact being added to a belief..
	 */
	public IFuture<ChangeInfo<?>> waitForFactX(String belname, String[] evtypes, long timeout, final IFilter<ChangeInfo<?>> filter)
	{
		Future<ChangeInfo<?>> ret = new Future<ChangeInfo<?>>();
		
//		final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)getAgent()).getInterpreter();
				
		// Also set waitabstraction to know what the plan is waiting for
		final List<EventType> ets = new ArrayList<EventType>();
		WaitAbstraction wa = new WaitAbstraction();
		for(String evtype: evtypes)
		{
			EventType et = new EventType(new String[]{evtype, belname});
			wa.addChangeEventType(et.toString());
			ets.add(et);
		}
//		setWaitAbstraction(wa);
		
		Object obj = getFromWaitqueue(wa);
		if(obj!=null)
		{
			ret.setResult((ChangeInfo<?>)((ChangeEvent)obj).getValue());
//			ret = new Future<Object>(obj);
		}
		else
		{
			final String rulename = getRuleName();
			
			final ResumeCommand<ChangeInfo<?>> rescom = new ResumeCommand<ChangeInfo<?>>(ret, rulename, false);
//			final ResumeCommand<Object> rescom = new ResumeCommand<Object>(ret, rulename, false);
//			setResumeCommand(rescom);
			addResumeCommand(rescom);
			
			IFuture<ITimer> cont = createTimer(timeout, getAgent(), rescom);
			cont.addResultListener(new DefaultResultListener<ITimer>()
			{
				public void resultAvailable(final ITimer timer)
				{
					if(timer!=null)
						rescom.setTimer(timer);
					
					Rule<Void> rule = new Rule<Void>(rulename, filter==null? ICondition.TRUE_CONDITION: new ICondition()
					{
						public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
						{
							return new Future<Tuple2<Boolean, Object>>(filter.filter((ChangeInfo<?>)event.getContent())? ICondition.TRUE: ICondition.FALSE);
						}
					}, new IAction<Void>()
					{
						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
						{
//							if(rescom.equals(getResumeCommand()))
//							{
								setDispatchedElement(new ChangeEvent(event));
								rescom.execute(null);
//								RPlan.executePlan(RPlan.this, getAgent(), rescom);
//							}
							return IFuture.DONE;
						}
					});
					
//					rule.addEvent(et);
					rule.setEvents(ets);
					getRuleSystem().getRulebase().addRule(rule);
				}
			});
		}
		
		Future<ChangeInfo<?>> fut = new BDIFuture<ChangeInfo<?>>();
		ret.addResultListener(new DelegationResultListener<ChangeInfo<?>>(fut)
		{
			public void customResultAvailable(ChangeInfo<?> result)
			{
//				ChangeEvent ce = (ChangeEvent)result;
//				super.customResultAvailable(ce.getValue());
				super.customResultAvailable(result);
			}
		});
		
		return fut;
	}
	
	/**
	 *  Wait for a fact being added or removed to a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactAddedOrRemoved(String belname)
	{
		return waitForFactAddedOrRemoved(belname, -1);
	}
	
	/**
	 *  Wait for a fact being added or removed to a belief.
	 */
	public IFuture<ChangeInfo<?>> waitForFactAddedOrRemoved(String belname, long timeout)
	{
		return waitForFactX(belname, new String[]{ChangeEvent.FACTADDED, ChangeEvent.FACTREMOVED}, timeout, null);
		
//		Future<ChangeInfo<?>> ret = new BDIFuture<ChangeInfo<?>>();
//		
//		// Also set waitabstraction to know what the plan is waiting for
//		WaitAbstraction wa = new WaitAbstraction();
//		final EventType eta = new EventType(new String[]{ChangeEvent.FACTADDED, belname});
//		final EventType etb = new EventType(new String[]{ChangeEvent.FACTREMOVED, belname});
//		wa.addChangeEventType(eta.toString());
//		wa.addChangeEventType(etb.toString());
////		setWaitAbstraction(wa);
//		
//		Object obj = getFromWaitqueue(wa);
//		if(obj!=null)
//		{
//			ret.setResult((ChangeInfo<?>)((ChangeEvent)obj).getValue());
////			ret = new Future<ChangeEvent>((ChangeEvent)obj);
//		}
//		else
//		{
//			final String rulename = getRuleName();
//			final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)getAgent()).getInterpreter();
//			
//			final ResumeCommand<ChangeInfo<?>> rescom = new ResumeCommand<ChangeInfo<?>>(ret, rulename, false);
////			setResumeCommand(rescom);
//			addResumeCommand(rescom);
//			
//			IFuture<ITimer> cont = createTimer(timeout, ip, rescom);
//			cont.addResultListener(new DefaultResultListener<ITimer>()
//			{
//				public void resultAvailable(final ITimer timer)
//				{
//					if(timer!=null)
//						rescom.setTimer(timer);
//					
//					Rule<Void> rule = new Rule<Void>(rulename, ICondition.TRUE_CONDITION, new IAction<Void>()
//					{
//						public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
//						{
////							if(rescom.equals(getResumeCommand()))
//							{
//								setDispatchedElement(new ChangeEvent(event));
//								RPlan.executePlan(RPlan.this, getAgent(), rescom);
//							}
//							return IFuture.DONE;
//						}
//					});
//					rule.addEvent(eta);
//					rule.addEvent(etb);
//					ip.getRuleSystem().getRulebase().addRule(rule);
//				}
//			});
//		}
//		
//		return ret;
	}
	
	/**
	 *  Wait for a collection change.
	 */
	public <T> IFuture<ChangeInfo<T>> waitForCollectionChange(String belname, long timeout, IFilter<ChangeInfo<T>> filter)
//	public IFuture<ChangeInfo<?>> waitForCollectionChange(String belname, long timeout, IFilter<ChangeInfo<?>> filter)
	{
		// buahhh :-((( how to get this generics nightmare?
		IFuture fut = waitForFactX(belname, new String[]{ChangeEvent.FACTCHANGED, ChangeEvent.FACTADDED, ChangeEvent.FACTREMOVED}, timeout, (IFilter)filter);
		return (IFuture<ChangeInfo<T>>)fut;
	}
	
	/**
	 *  Wait for a collection change.
	 */
	public <T> IFuture<ChangeInfo<T>> waitForCollectionChange(String belname, long timeout, final Object id)
	{
		IFuture fut = waitForFactX(belname, new String[]{ChangeEvent.FACTCHANGED, ChangeEvent.FACTADDED, ChangeEvent.FACTREMOVED}, timeout, new IFilter<ChangeInfo<?>>()
		{
			public boolean filter(ChangeInfo<?> info)
			{
				boolean ret = false;
				if(info.getInfo()!=null)
				{
					ret = info.getInfo().equals(id);
				}
				return ret;
			}
		});
		return (IFuture<ChangeInfo<T>>)fut;
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
		Future<Void> ret = new BDIFuture<Void>();

//		final BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)getAgent()).getInterpreter();
		
		final String rulename = getRuleName();
		
		final ResumeCommand<Void> rescom = new ResumeCommand<Void>(ret, rulename, false);
//			setResumeCommand(rescom);
		addResumeCommand(rescom);
		
		IFuture<ITimer> cont = createTimer(timeout, getAgent(), rescom);
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
//						if(rescom.equals(getResumeCommand()))
//						{
							setDispatchedElement(new ChangeEvent(event));
							rescom.execute(null);
//							RPlan.executePlan(RPlan.this, getAgent(), rescom);
//						}
						return IFuture.DONE;
					}
				});
				for(String ev: events)
				{
					rule.addEvent(new EventType(ev));
				}
				getRuleSystem().getRulebase().addRule(rule);
			}
		});
		return ret;
	}
	
	
	
	/**
	 * 
	 */
	public IFuture<ITimer> createTimer(long timeout, final IInternalAccess ia, final ICommand<ResumeCommandArgs> rescom)
	{
		final Future<ITimer> ret = new Future<ITimer>();
		if(timeout>-1)
		{
			IClockService	cs	= ia.getFeature(IRequiredServicesFeature.class).getLocalService(new ServiceQuery<>(IClockService.class));
			ITimedObject to	= new ITimedObject()
			{
				public void timeEventOccurred(long currenttime)
				{
//					if(rescom.equals(getResumeCommand()))
//					{
//						setException(new TimeoutException());
						rescom.execute(new ResumeCommandArgs(null, null, new TimeoutException()));
//						RPlan.executePlan(RPlan.this, ia, rescom);
//					}
				}
			};
			
			ITimer timer = cs.createTimer(timeout, to);
			ret.setResult(timer);
			
//			IFuture<ITimer> tfut = ia.getComponentFeature(IExecutionFeature.class).waitForDelay(timeout, new IComponentStep<Void>()
//			{
//				public IFuture<Void> execute(IInternalAccess ia)
//				{
////					if(rescom.equals(getResumeCommand()))
//					{
//						setException(new TimeoutException());
//						RPlan.executePlan(RPlan.this, ia, rescom);
//					}
//					return IFuture.DONE;
//				}
//			});
			
//			IFuture<ITimer> tfut = ((BDIAgent)ia).waitFor(timeout, new IComponentStep<Void>()
//			{
//				public IFuture<Void> execute(IInternalAccess ia)
//				{
////					if(rescom.equals(getResumeCommand()))
//					{
//						setException(new TimeoutException());
//						RPlan.executePlan(RPlan.this, ia, rescom);
//					}
//					return IFuture.DONE;
//				}
//			});
//			tfut.addResultListener(new DefaultResultListener<ITimer>()
//			{
//				public void resultAvailable(ITimer result)
//				{
//					ret.setResult(result);
//				}
//			});
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}
	
//	/**
//	 *  Wait for some time.
//	 */
//	public IFuture<ITimer> waitForDelayWithTimer(final long delay, final IComponentStep<?> step)
//	{
//		final Future<ITimer> ret = new Future<ITimer>();
//		
//		IClockService cs = getComponent().getComponentFeature(IRequiredServicesFeature.class).getLocalService(new ServiceQuery<>( IClockService.class, ServiceScope.PLATFORM));
//		ITimedObject	to	=  	new ITimedObject()
//		{
//			public void timeEventOccurred(long currenttime)
//			{
//				scheduleStep(step);
//			}
//			
//			public String toString()
//			{
//				return "waitForDelay("+getComponent().getComponentIdentifier()+")";
//			}
//		};
//		
//		ITimer timer = cs.createTimer(delay, to);
//		ret.setResult(timer);
//		
//		return ret;
//	}
	
//	/**
//	 * 
//	 */
//	public <T> IFuture<T> invokeInterruptable(IResultCommand<IFuture<T>, Void> command)
//	{
//		final Future<T> ret = new BDIFuture<T>();
//		
//		final ICommand<Tuple2<Boolean, Boolean>> rescom = new ResumeCommand<T>(ret, null, false);
////		setResumeCommand(rescom);
//		addResumeCommand(rescom);
//		
//		command.execute(null).addResultListener(getAgent().getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<T>()
//		{
//			public void resultAvailable(T result)
//			{
////				if(rescom.equals(getResumeCommand()))
//				{
//					setDispatchedElement(result);
//					rescom.execute(null);
////					RPlan.executePlan(RPlan.this, ia, rescom);
//				}
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
////				if(rescom.equals(getResumeCommand()))
//				{
//					setException(exception);
//					rescom.execute(null);
////					RPlan.executePlan(RPlan.this, ia, rescom);
//				}
//			}
//		}));
//		
//		return ret;
//	}
	
	/**
	 * 
	 * @return
	 */
	protected String getRuleName()
	{
		return getId()+"_wait_#"+cnt++;
	}
	
	/**
	 *  Called before blocking the component thread.
	 */
	public void	beforeBlock()
	{
		testBodyAborted();
		ComponentSuspendable sus = ComponentSuspendable.COMSUPS.get();
		if(sus!=null && !RPlan.PlanProcessingState.WAITING.equals(getProcessingState()))
		{
			final ResumeCommand<Void> rescom = new ResumeCommand<Void>(sus, false);
			setProcessingState(PlanProcessingState.WAITING);
//			System.out.println("setting rescom: "+getId()+" "+rescom);
			resumecommand = rescom;
		}
	}
	
	/**
	 *  Called after unblocking the component thread.
	 */
	public void	afterBlock()
	{
		testBodyAborted();
		setProcessingState(PlanProcessingState.RUNNING);
		setWaitAbstraction(null);
		if(resumecommand!=null)
		{
			// performs only cleanup without setting future
//			System.out.println("afterblock rescom: "+getId()+" "+resumecommand);
			resumecommand.execute(new ResumeCommandArgs(Boolean.FALSE, null, null));
//			resumecommand.execute(new Tuple2<Boolean, Boolean>(Boolean.FALSE, null));
			resumecommand = null;
		}
	}

	/**
	 *  Check if plan is already aborted.
	 */
	protected void testBodyAborted()
	{
//		if(agent.toString().indexOf("Leaker")!=-1)
//		{
//			System.out.println("testBodyAborted "+this);
//		}
		// Throw error to exit body method of aborted plan.
		if(isFinishing() && PlanLifecycleState.BODY.equals(getLifecycleState()))
		{
			BodyAborted	ba	= new BodyAborted();
//			try
//			{
//				if(agent.toString().indexOf("Leaker")!=-1)
//				{
//					System.out.println("before throw BodyAborted: "+Runtime.getRuntime().freeMemory());
//				}
				throw ba;
//			}
//			finally
//			{
//				if(agent.toString().indexOf("Leaker")!=-1)
//				{
//					System.out.println("after throw BodyAborted: "+Runtime.getRuntime().freeMemory());
//				}
//			}
		}
	}

	public static class ResumeCommandArgs
	{
		protected Boolean notify;
		
		protected Boolean abort;
		
		protected Exception exception;
		
		public ResumeCommandArgs(Boolean notify, Boolean abort, Exception exception)
		{
			this.notify = notify;
			this.abort = abort;
			this.exception = exception;
		}

		/**
		 *  Get the notify.
		 *  @return the notify
		 */
		public Boolean getNotify()
		{
			return notify;
		}

		/**
		 *  Get the abort.
		 *  @return the abort
		 */
		public Boolean getAbort()
		{
			return abort;
		}

		/**
		 *  Get the exception.
		 *  @return the exception
		 */
		public Exception getException()
		{
			return exception;
		}
	}
	
	/**
	 * 
	 */
	public class ResumeCommand<T> implements ICommand<ResumeCommandArgs>
	{
		protected ComponentSuspendable sus;
		protected Future<T> waitfuture;
		protected String rulename;
		protected ITimer timer;
		protected boolean isvoid;
		
		public ResumeCommand(Future<T> waitfuture, boolean isvoid)
		{
			this(waitfuture, null, isvoid);
		}
		
		public ResumeCommand(Future<T> waitfuture, String rulename, boolean isvoid)
		{
//			System.out.println("created: "+this+" "+RPlan.this.getId());
			this.waitfuture = waitfuture;
			this.rulename = rulename;
			this.isvoid = isvoid;
		}
		
		public ResumeCommand(ComponentSuspendable sus, boolean isvoid)
		{
			this.sus = sus;
			this.waitfuture = (Future<T>)sus.getFuture();
			this.isvoid = isvoid;
		}
		
		public void setTimer(ITimer timer)
		{
			this.timer = timer;
		}

		/**
		 *  first Boolean: notify (default true)
		 *  second Boolean: abort (default false)
		 */
		public void execute(ResumeCommandArgs args)
		{
			assert getAgent().getFeature(IExecutionFeature.class).isComponentThread();

//			System.out.println("exe: "+this+" "+RPlan.this.getId()+" "+this);

			Exception ex = args!=null? args.getException(): null;
			
			if(rulename!=null)
			{
//				System.out.println("rem rule: "+rulename);
//				BDIAgentInterpreter ip = (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
				getRuleSystem().getRulebase().removeRule(rulename);
			}
			if(timer!=null)
			{
				timer.cancel();
			}
			waitabstraction = null;
			
			boolean notify = args!=null && args.getNotify()!=null? args.getNotify().booleanValue(): true;
			boolean abort = args!=null && args.getAbort()!=null? args.getAbort().booleanValue(): sus!=null;
			
			if(notify && RPlan.PlanProcessingState.WAITING.equals(getProcessingState()))
			{
				boolean donotify = false;
				if(resumecommands!=null && resumecommands.contains(this))
				{
					resumecommands.remove(this);
					donotify = true;
				}
				if(this.equals(resumecommand))
				{
//					System.out.println("clear rescom: "+getId());
					resumecommand = null;
					donotify = true;
				}
				
				if(donotify)
				{
					if(!abort)//sus==null)
					{
						if(ex!=null)
						{
							if(waitfuture instanceof ITerminableFuture)
							{
//								System.out.println("notify1: "+getId());
								((ITerminableFuture<?>)waitfuture).terminate(ex);
							}
							else
							{
//								System.out.println("notify2: "+getId());
								waitfuture.setExceptionIfUndone(ex);
							}
							
//							setException(null);	// Allow plan to continue when exception is catched.
						}
						else
						{
							Object o = getDispatchedElement();
							if(o instanceof ChangeEvent)
							{
								o = ((ChangeEvent)o).getValue();
							}
//							System.out.println("notify3: "+getId());
							waitfuture.setResultIfUndone(isvoid? null: (T)o);
						}
					}
					else
					{
//						System.out.println("notify4: "+getId());
						waitfuture.abortGet(sus);
					}
				}
			}
		}

		/**
		 *  Get the waitfuture.
		 *  @return The waitfuture
		 */
		public Future<T> getWaitfuture()
		{
			return waitfuture;
		}
	}
	
//	/**
//	 * 
//	 */
//	public void addPlanListener(IPlanListener<?> listener)
//	{
//		if(listeners==null)
//			listeners = new ArrayList<IPlanListener<?>>();
//		listeners.add(listener);
//	}
	
	/**
	 * 
	 */
	public void addResumeCommand(ICommand<ResumeCommandArgs> rescom)
	{
//		System.out.println("addResCom: "+this);
		if(resumecommands==null)
			resumecommands = new ArrayList<ICommand<ResumeCommandArgs>>();
		resumecommands.add(rescom);
	}
	
	/**
	 * 
	 */
	public void removeResumeCommand(ICommand<Tuple2<Boolean, Boolean>> rescom)
	{
		if(resumecommands!=null)
			resumecommands.remove(rescom);
	}

	/**
	 *  Get the resumecommands.
	 *  @return The resumecommands.
	 */
	public List<ICommand<ResumeCommandArgs>> getResumeCommands()
	{
		return resumecommands;
	}
	
	/**
	 *  Get the resumecommand.
	 *  @return The resumecommand.
	 */
	public ICommand<ResumeCommandArgs> getResumeCommand()
	{
		return resumecommand;
	}
	
	/**
	 *  Get the result.
	 *  @return The result.
	 */
	public Object getResult()
	{
		return result;
	}

	/**
	 *  Set the result.
	 *  @param result The result to set.
	 */
	public void setResult(Object result)
	{
		this.result = result;
	}
	
	/**
	 *  Get the atomic.
	 *  @return The atomic
	 */
	public boolean isAtomic()
	{
		return atomic;
	}

	/**
	 *  The atomic to set.
	 *  @param atomic The atomic to set
	 */
	public void setAtomic(boolean atomic)
	{
		this.atomic = atomic;
	}

	/**
	 *  Publish a tool event.
	 */
	public void publishToolPlanEvent(String evtype)
	{
		if(getAgent().getFeature0(IMonitoringComponentFeature.class)!=null 
			&& getAgent().getFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOSUBSCRIBERS, PublishEventLevel.FINE))
		{
			long time = System.currentTimeMillis();//getClockService().getTime();
			MonitoringEvent mev = new MonitoringEvent();
			mev.setSourceIdentifier(getAgent().getId());
			mev.setTime(time);
			
			PlanInfo info = PlanInfo.createPlanInfo(this);
			mev.setType(evtype+"."+IMonitoringEvent.SOURCE_CATEGORY_PLAN);
//			mev.setProperty("sourcename", element.toString());
			mev.setProperty("sourcetype", info.getType());
			mev.setProperty("details", info);
			mev.setLevel(PublishEventLevel.FINE);
			
			getAgent().getFeature(IMonitoringComponentFeature.class).publishEvent(mev, PublishTarget.TOSUBSCRIBERS);
		}
	}
	
	/**
	 *  Set up a rule for the waitque to signal to what kinds of events this plan
	 *  in principle reacts to.
	 */
	public void setupEventsRule(Collection<String> events)
	{
		List<EventType> evs = new ArrayList<EventType>();
		for(String event: events)
		{
			evs.add(new EventType(event));
		}
		internalSetupEventsRule(evs);
	}
	
	/**
	 *  Set up a rule for the waitqueue to signal to what kinds of events this plan
	 *  in principle reacts to.
	 */
	public void internalSetupEventsRule(List<EventType> events)
	{
		final String rulename = getId()+"_waitqueue";
		
		Rule<Void> rule = new Rule<Void>(rulename, ICondition.TRUE_CONDITION, new IAction<Void>()
		{
			public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
			{
//				System.out.println("Added to waitqueue: "+event);
				addToWaitqueue(new ChangeEvent(event));				
				return IFuture.DONE;
			}
		});
		rule.setEvents(events);
		getAgent().getFeature(IInternalBDIAgentFeature.class).getRuleSystem().getRulebase().updateRule(rule);
	}
	
//	/**
//	 *  Get the exception.
//	 *  @return The exception.
//	 */
//	public Exception getException();
	
	/**
	 *  Test if element is succeeded.
	 *  @return True, if is succeeded.
	 */
	public boolean	isSucceeded()
	{
		return isPassed();
	}
	
	/**
	 *  Check if the element is currently part of the agent's reasoning.
	 *  E.g. the bases are always adopted and all of their contents such as goals, plans and beliefs.
	 */
	public boolean	isAdopted()
	{
		return true;
//	 	// Hack!!! Subgoals removed to late, TODO: fix hierarchic goal plan lifecycle management
//		System.out.println(this + " isAdopted(): "+(!(getReason() instanceof RParameterElement) || ((RParameterElement) getReason()).isAdopted()));
//		return !(getReason() instanceof RParameterElement) || ((RParameterElement) getReason()).isAdopted();
	}
	
//	/**
//	 * 
//	 */
//	public BDIAgentInterpreter getInterpreter()
//	{
//		return (BDIAgentInterpreter)((BDIAgent)ia).getInterpreter();
//	}
	
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
	
	/**
	 *  Future that overrides addResultListener to keep track
	 *  of current rplan in RPLANS variable.
	 */
	public class BDIFuture<E> extends Future<E>
	{
		/**
		 *  Add a listener
		 *  @param listener The listener. 
		 */
		public void addResultListener(IResultListener<E> listener) 
		{
			super.addResultListener(new BDIComponentResultListener<E>(listener, getAgent()));
		}
	}
	
	/**
	 *  Waitque holds events for later processing.
	 */
	
	public class Waitqueue
	{
		protected List<Object>	queue	= new ArrayList<Object>();
		
		public String toString()
		{
			return "Waitqueue("+RPlan.this+", "+queue.toString()+")";
		}

		public RPlan getPlan()
		{
			return RPlan.this;
		}

		public void addElement(Object element)
		{
			queue.add(element);
		}
		
		/**
		 *  Test if waitqueue is empty.
		 */
		public boolean isEmpty()
		{
			return queue.isEmpty();
		}
		
		/**
		 *  Get the currently contained elements of the waitqueue.
		 *  @return The collected elements.
		 */
		public Object[] getElements()
		{
			return queue.toArray();
		}

		/**
		 * 
		 */
		protected Object getFromWaitqueue(WaitAbstraction wa)
		{
			Object ret = null;
			for(int i=0; i<queue.size(); i++)
			{
				Object obj = queue.get(i);
				if(wa.isWaitingFor(obj))
				{
					ret = obj;
					queue.remove(i);
					break;
				}
			}
			return ret;
		}
	}
}
