package jadex.bdibpmn;

import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.IElement;
import jadex.bdi.runtime.IEventbase;
import jadex.bdi.runtime.IExpression;
import jadex.bdi.runtime.IExpressionbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalbase;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IParameter;
import jadex.bdi.runtime.IParameterSet;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.IPlanListener;
import jadex.bdi.runtime.IPlanbase;
import jadex.bdi.runtime.IWaitqueue;
import jadex.bdi.runtime.PlanFailureException;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.BeliefbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.CapabilityFlyweight;
import jadex.bdi.runtime.impl.flyweights.EventbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.ExpressionFlyweight;
import jadex.bdi.runtime.impl.flyweights.ExpressionNoModel;
import jadex.bdi.runtime.impl.flyweights.ExpressionbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.ExternalAccessFlyweight;
import jadex.bdi.runtime.impl.flyweights.GoalFlyweight;
import jadex.bdi.runtime.impl.flyweights.GoalbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.InternalEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.MessageEventFlyweight;
import jadex.bdi.runtime.impl.flyweights.ParameterFlyweight;
import jadex.bdi.runtime.impl.flyweights.ParameterSetFlyweight;
import jadex.bdi.runtime.impl.flyweights.PlanFlyweight;
import jadex.bdi.runtime.impl.flyweights.PlanbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.WaitqueueFlyweight;
import jadex.bdi.runtime.interpreter.GoalLifecycleRules;
import jadex.bdi.runtime.interpreter.InternalEventRules;
import jadex.bdi.runtime.interpreter.MessageEventRules;
import jadex.bdi.runtime.interpreter.OAVBDIFetcher;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdibpmn.handler.DummyCancelable;
import jadex.bdibpmn.handler.EventIntermediateMessageActivityHandler;
import jadex.bdibpmn.handler.EventIntermediateRuleActicityHandler;
import jadex.bdibpmn.handler.EventIntermediateSignalActivityHandler;
import jadex.bdibpmn.handler.EventIntermediateTimerActivityHandler;
import jadex.bpmn.features.impl.BpmnComponentFeature;
import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.bpmn.model.MPool;
import jadex.bpmn.model.MSequenceEdge;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.handler.AbstractEventIntermediateTimerActivityHandler;
import jadex.bpmn.runtime.handler.CompositeCancelable;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.IFuture;
import jadex.javaparser.IExpressionParser;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.rules.state.IOAVState;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  A BPMN instance that is executed as a plan body.
 */
public class BpmnPlanBodyInstance extends BpmnComponentFeature
{
	//-------- static part --------
	
	/** Identifier for an undefined pool (e.g. when no 'aborted' pool is specified). */
	protected static final String	POOL_UNDEFINED	= "undefined-pool";
	
	/** The activity execution handlers (activity type -> handler). */
	public static final Map	PLAN_ACTIVITY_HANDLERS;
	
	static
	{
		Map	defhandlers	= new HashMap(BpmnComponentFeature.DEFAULT_ACTIVITY_HANDLERS);
		defhandlers.put(MBpmnModel.EVENT_START_TIMER, new EventIntermediateTimerActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_TIMER, new EventIntermediateTimerActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_MESSAGE, new EventIntermediateMessageActivityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_RULE, new EventIntermediateRuleActicityHandler());
		defhandlers.put(MBpmnModel.EVENT_INTERMEDIATE_SIGNAL, new EventIntermediateSignalActivityHandler());
		PLAN_ACTIVITY_HANDLERS	= Collections.unmodifiableMap(defhandlers);
	}
	
	//-------- attributes --------
	
	/** The bdi interpreter. */
	protected IInternalBDIAgentFeature interpreter;
	
	/** The last plan lifecycle state. */
	protected String lifecyclestate;
	
	/** The wait times of waiting threads (thread -> absolute timepoint). */
	protected Map	waittimes;
	
	/** The runtime plan element. */
	protected Object rplan;
	
	/** The runtime capability. */
	protected Object rcapa ;
	
	/** The state. */
	protected IOAVState state;
	
	//-------- constructors --------
	
	/**
	 *  Create a new BPMN process instance using default handler.
	 *  @param model	The BMPN process model.
	 */
	public BpmnPlanBodyInstance(MBpmnModel model, final BDIInterpreter interpreter, 
		final Object rcapa, final Object rplan)
	{
		super(interpreter.getAgentAdapter(), model, null, null, interpreter.getParent(), PLAN_ACTIVITY_HANDLERS, 
			null, new OAVBDIFetcher(interpreter.getState(), rcapa, rplan), 
			interpreter.getCMS(), interpreter.getClockService(), interpreter.getMessageService(), 
			interpreter.getServiceContainer(), interpreter.getServiceContainer().getServiceRegistry());
		this.interpreter	= interpreter;
		this.state = interpreter.getState();
		this.rcapa = rcapa;
		this.rplan = rplan;
//		this.creationtime = interpreter.getCreationTime();
		
		// todo:
		
//		this.setAdapter(new IBpmnExecutor()
//		{
//			public void wakeUp()
//			{
//				if(interpreter.isExternalThread())
//				{
////					Thread.dumpStack();
//					interpreter.invokeLater(new Runnable()
//					{
//						public void run()
//						{
//							if(state.containsObject(rplan) && !OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED.equals(state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate)))
//							{
//								String lane	= getLane(getLastState());
//								if(!LANE_UNDEFINED.equals(lane) && isReady(null, lane))
//								{
//									// todo: event?!
//									EventProcessingRules.schedulePlanInstanceCandidate(state, null, rplan, rcapa);
////									state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
//								}
//							}
//						}
//					});
//				}
//				else
//				{
//					if(state.containsObject(rplan) && !OAVBDIRuntimeModel.PLANPROCESSINGTATE_FINISHED.equals(state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate)))
//					{
//						String lane	= getLane(getLastState());
//						if(!LANE_UNDEFINED.equals(lane) && isReady(null, lane))
//						{
//							// todo: event?!
//							EventProcessingRules.schedulePlanInstanceCandidate(state, null, rplan, rcapa);
////							state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
//						}
//					}
//				}
//			}
//		});
	}
	
	//-------- methods --------

	/**
	 *  Get the last plan lifecycle state.
	 *  @return The plan lifecycle state.
	 */
	public String	getLastState()
	{
		return lifecyclestate;
	}
	
	/**
	 *  Set the plan lifecycle state.
	 *  @param state	The plan lifecycle state.
	 */
	public void	setLastState(String state)
	{
		this.lifecyclestate	= state;
	}
	
	/**
	 *  Add a timer for a thread.
	 *  @param thread	The process thread that should wait.
	 *  @param duration	The duration to wait for.
	 */
	public void addTimer(final ProcessThread thread, final long duration)
	{
		assert duration==EventIntermediateTimerActivityHandler.TICK_TIMER || duration>=0;
		
		if(waittimes==null)
			waittimes	= new HashMap();

		// how to support tick timer?
		if(duration==EventIntermediateTimerActivityHandler.TICK_TIMER)
			throw new UnsupportedOperationException("Tick timers for bdi-bpmn have to be implemented.");
		
		// todo: check if asynchronous handling of waittimes is a problem?
		
		IClockService clock = interpreter.getClockService();
		Long ret = Long.valueOf(clock.getTime()+duration);
		waittimes.put(thread, ret);
		
//		interpreter.getServiceProvider().searchService( new ServiceQuery<>( IClockService.class))
//			.addResultListener(interpreter.createResultListener(new DefaultResultListener()
//		{
//			public void resultAvailable(Object source, Object result)
//			{
//				IClockService clock = (IClockService)result;
//				Long ret = new Long(clock.getTime()+duration);
//				waittimes.put(thread, ret);
//			}
//		}));
	}
	
	/**
	 *  Remove a timer for a thread.
	 *  @param thread	The process thread that should wait.
	 *  @param duration	The duration to wait for.
	 */
	public void	removeTimer(ProcessThread thread)
	{
		if(waittimes!=null)
			waittimes.remove(thread);
	}
	
	/**
	 *  Update the waiting threads according to the wakeup reason (dispatched element).
	 *  The corresponding waiting activity/thread is notified. 
	 */
	public void updateWaitingThreads()
	{
		if(waittimes!=null)
		{
//			IClockService clock = (IClockService)interpreter.getServiceProvider().getService(IClockService.class);
			
			IClockService clock = interpreter.getClockService();
			for(Iterator it=waittimes.keySet().iterator(); it.hasNext(); )
			{
				ProcessThread	thread	= (ProcessThread)it.next();
				if(((Number)waittimes.get(thread)).longValue()<=clock.getTime())
				{
					it.remove();
					assert thread.isWaiting();
					BpmnPlanBodyInstance.this.notify(thread.getActivity(), thread, AbstractEventIntermediateTimerActivityHandler.TIMER_EVENT);
				}
			}
		}
		
		Object dispelem = state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement);
//		System.out.println("dispatched: "+dispelem);
		
		if(dispelem!=null)
		{
			for(Iterator<ProcessThread> it=getTopLevelThread().getAllThreads().iterator(); it.hasNext(); )
			{
				ProcessThread thread = (ProcessThread)it.next();
				try
				{
					if(thread.isWaiting() && thread.getWaitFilter()!=null && thread.getWaitFilter().filter(dispelem))
					{
						BpmnPlanBodyInstance.this.notify(thread.getActivity(), thread, getFlyweight(dispelem));
					}
				}
				catch(Exception e)
				{
					// just catch filter exceptions.
				}
			}
		}
	}

	/**
	 *  Get the current timeout of the process, i.e. the
	 *  remaining time of the closest due intermediate timer event. 
	 *  @return The current timeout or -1 for no timeout.
	 */
	public long getTimeout()
	{
		long	mindur	= -1;
		if(waittimes!=null)
		{
			String pool	= getPool(getLastState());
			if(!POOL_UNDEFINED.equals(pool))
			{
				IClockService	clock = interpreter.getClockService();
//				IClockService	clock	= (IClockService)interpreter.getServiceProvider().getService(IClockService.class);
				for(Iterator it=waittimes.keySet().iterator(); it.hasNext(); )
				{
					ProcessThread	thread	= (ProcessThread) it.next();
					if(thread.belongsTo(pool, null))
					{
						long	time	= Math.max(((Number)waittimes.get(thread)).longValue()-clock.getTime(), 0);
						mindur	= mindur==-1 ? time : time<mindur ? time : mindur;
					}
				}
			}
		}
		return mindur;
	}
	
	/**
	 *  Get the cumulated wait abstraction for all threads.
	 *  @return The wait abstraction.
	 */
	public Object  getWaitAbstraction()
	{
		Object ret	= null;
		String pool	= getPool(getLastState());
		if(!POOL_UNDEFINED.equals(pool))
		{
			ret = getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			boolean empty = true;
			
			for(Iterator<ProcessThread> it=getTopLevelThread().getAllThreads().iterator(); it.hasNext(); )
			{
				ProcessThread pt = it.next();
				if(pt.isWaiting() && pt.belongsTo(pool, null))
				{
					MActivity act = pt.getActivity();		
					
					if(MBpmnModel.EVENT_INTERMEDIATE_MESSAGE.equals(act.getActivityType()))
					{
						DummyCancelable da = (DummyCancelable)pt.getWaitInfo();
						String type = da.getType();
						if(type==null)
							throw new RuntimeException("Message type not specified: "+act);
						SFlyweightFunctionality.addMessageEvent(ret, type, state, rcapa);
						empty = false;
					}
					else if(MBpmnModel.EVENT_INTERMEDIATE_SIGNAL.equals(act.getActivityType()))
					{
						DummyCancelable da = (DummyCancelable)pt.getWaitInfo();
						String type = da.getType();
						if(type==null)
							throw new RuntimeException("Internal event type not specified: "+type);
						SFlyweightFunctionality.addInternalEvent(ret, type, state, rcapa);
						empty = false;
					}
					else if(MBpmnModel.EVENT_INTERMEDIATE_RULE.equals(act.getActivityType()))
					{
						DummyCancelable da = (DummyCancelable)pt.getWaitInfo();
						String type = da.getType();
						if(type==null)
							throw new RuntimeException("Rule type not specified: "+type);
						SFlyweightFunctionality.addCondition(ret, type, state, rcapa);
						empty = false;
					}
					else if(MBpmnModel.EVENT_INTERMEDIATE_MULTIPLE.equals(act.getActivityType()))
					{
						List edges = pt.getActivity().getOutgoingSequenceEdges();
						CompositeCancelable was = (CompositeCancelable)pt.getWaitInfo();
		
						for(int i=0; i<edges.size(); i++)
						{
							DummyCancelable sca = (DummyCancelable)was.getSubcancelInfos()[i];
							MSequenceEdge edge = (MSequenceEdge)edges.get(i);
							MActivity nextact = edge.getTarget();
							if(MBpmnModel.EVENT_INTERMEDIATE_MESSAGE.equals(nextact.getActivityType()))
							{
								String type = (String)sca.getType();
								if(type==null)
									throw new RuntimeException("Message type not specified: "+type);
								SFlyweightFunctionality.addMessageEvent(ret, type, state, rcapa);
								empty = false;
							}
							else if(MBpmnModel.EVENT_INTERMEDIATE_SIGNAL.equals(nextact.getActivityType()))
							{
								String type = (String)sca.getType();
								if(type==null)
									throw new RuntimeException("Internal event type not specified: "+type);
								SFlyweightFunctionality.addInternalEvent(ret, type, state, rcapa);
								empty = false;
							}
							else if(MBpmnModel.EVENT_INTERMEDIATE_RULE.equals(nextact.getActivityType()))
							{
								String type = (String)sca.getType();
								if(type==null)
									throw new RuntimeException("Rule type not specified: "+type);
								SFlyweightFunctionality.addCondition(ret, type, state, rcapa);
								empty = false;
							}
							else if(MBpmnModel.EVENT_INTERMEDIATE_TIMER.equals(nextact.getActivityType()))
							{
								// nothing to do with waitqueue.
							}
							else
							{
								throw new RuntimeException("Unknown event: "+nextact);
							}
						}
					}
					
					// todo: condition wait
					
					// todo: time wait?!
				}
			}
			
			if(empty)
			{
				state.dropObject(ret);
				ret = null;
			}
		}
		
		return ret;
	}
	
	//-------- bdi plan methods --------
	
	/**
	 *  Let a plan fail.
	 */
	public void fail()
	{
		throw new PlanFailureException();
	}

	/**
	 *  Let a plan fail.
	 *  @param cause The cause.
	 */
	public void fail(Throwable cause)
	{
		throw new PlanFailureException(null, cause);
	}

	/**
	 *  Let a plan fail.
	 *  @param message The message.
	 *  @param cause The cause.
	 */
	public void fail(String message, Throwable cause)
	{
		throw new PlanFailureException(message, cause);
	}

	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	public ICapability getScope()
	{
		return new CapabilityFlyweight(state, rcapa);
	}

	/**
	 *  Get the reason, why the plan gets executed.
	 */
	public IElement getReason()
	{
		Object elem = state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_reason);
		return getFlyweight(elem);
	}
	
	/**
	 *  Get the dispatched element, i.e. the element that caused
	 *  the current plan step being executed.
	 *  @return The dispatched element.
	 */
	public IElement getDispatchedElement()
	{
		Object elem = state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement);
		return getFlyweight(elem);
	}
	
	/**
	 *  Get flyweight for an element.
	 *  @param elem The element.
	 *  @return The flyweight.
	 */
	protected IElement getFlyweight(Object elem)
	{
		IElement ret = null;
		
		if(elem!=null)
		{
			// todo: wrong scope
			ret = SFlyweightFunctionality.getFlyweight(state, rcapa, elem);
		}
		
		return ret;
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return BDIAgentFeature.getInterpreter(state).getLogger(rcapa);
	}

	
	/**
	 *  Start an atomic transaction.
	 *  All possible side-effects (i.e. triggered conditions)
	 *  of internal changes (e.g. belief changes)
	 *  will be delayed and evaluated after endAtomic() has been called.
	 *  @see #endAtomic()
	 */
	public void	startAtomic()
	{
		interpreter.startMonitorConsequences();
		interpreter.startAtomic();
	}

	/**
	 *  End an atomic transaction.
	 *  Side-effects (i.e. triggered conditions)
	 *  of all internal changes (e.g. belief changes)
	 *  performed after the last call to startAtomic()
	 *  will now be evaluated and performed.
	 *  @see #startAtomic()
	 */
	public void	endAtomic()
	{
		interpreter.endAtomic();
		interpreter.endMonitorConsequences();
	}

	/**
	 *  Dispatch a new subgoal.
	 *  @param subgoal The new subgoal.
	 *  @return The eventfilter for identifying the result event.
	 *  Note: plan step is interrupted after call.
	 * /
	public IFilter dispatchSubgoal(IGoal subgoal)
	{
		rplan.getScope().getAgent().startMonitorConsequences();

		try
		{
			IRGoal original = (IRGoal)((GoalWrapper)subgoal).unwrap(); // unwrap!!!
			return rplan.getScope().getGoalbase().dispatchSubgoal(rplan.getRootGoal(), original);
		}
		catch(GoalFailureException gfe)
		{
			gfe.setGoal(subgoal);
			throw gfe;
		}
		finally
		{
			// Interrupts the plan step, if necessary.
			rplan.getScope().getAgent().endMonitorConsequences();
		}
	}*/
	
	/**
	 *  Dispatch a new subgoal.
	 *  @param subgoal The new subgoal.
	 *  Note: plan step is interrupted after call.
	 */
	public void dispatchSubgoal(IGoal subgoal)
	{
		Object rgoal = ((GoalFlyweight)subgoal).getHandle();
		Object scope = ((GoalFlyweight)subgoal).getScope();
		interpreter.startMonitorConsequences();
		GoalLifecycleRules.adoptGoal(state, scope, rgoal);
		state.addAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_subgoals, rgoal);
		state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_parentplan, rplan);

		// Protect goal, if necessary.
		Object	planstate	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_lifecyclestate);
		Object	reason	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_reason);
		boolean	protectgoal	= OAVBDIRuntimeModel.PLANLIFECYCLESTATE_PASSED.equals(planstate)
			|| OAVBDIRuntimeModel.PLANLIFECYCLESTATE_FAILED.equals(planstate)
			|| OAVBDIRuntimeModel.PLANLIFECYCLESTATE_ABORTED.equals(planstate);
		if(!protectgoal && reason!=null && state.getType(reason).isSubtype(OAVBDIRuntimeModel.goal_type))
		{
			 protectgoal	= ((Boolean)state.getAttributeValue(reason, OAVBDIRuntimeModel.goal_has_protected)).booleanValue();
		}
		if(protectgoal)
		{
			state.setAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_protected, Boolean.TRUE);
		}
	
		interpreter.endMonitorConsequences();
	}

	/**
	 *  Get the name.
	 *  @return The name of the plan.
	 * /
	public String getName()
	{
		Object	mplan	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
		String	mname	= (String)state.getAttributeValue(mplan, OAVBDIMetaModel.modelelement_has_name);
		return mname+"_"+rplan;
	}*/

	/**
	 *  todo: remove
	 *  Get the plans root goal.
	 *  @return The goal.
	 * /
	public IProcessGoal getRootGoal()
	{
		if(rootgoal==null)
			rootgoal = new ProcessGoalWrapper(rplan.getRootGoal());
		return rootgoal;
	}*/

	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 */
	public IWaitqueue getWaitqueue()
	{
		return WaitqueueFlyweight.getWaitqueueFlyweight(getState(), getRCapability(), getRPlan());
	}

	/**
	 *  Add some code to the agent's agenda,
	 *  that will be executed on the agent's thread.
	 *  This method can safely be called from any thread
	 *  (e.g. AWT event handlers).
	 *  todo: remove
	 * /
	public void	invokeLater(Runnable code)
	{
		rplan.getScope().getAgent().invokeLater(code);
	}*/

	/**
	 *  Add some code to the agent's agenda,
	 *  and wait until it has been executed on the agent's thread.
	 *  This method can safely be called from any thread
	 *  (e.g. AWT event handlers).
	 *  todo: remove
	 * /
	public void	invokeAndWait(Runnable code)
	{
		rplan.getScope().getAgent().invokeAndWait(code);
	}*/

	/**
	 *  Get the agent name.
	 *  @return The agent name.
	 */
	public String getAgentName()
	{
		return getAgentIdentifier().getLocalName();
	}
	
	/**
	 * Get the agent identifier.
	 * @return The agent identifier.
	 */
	public IComponentIdentifier	getAgentIdentifier()
	{
		return interpreter.getAgentAdapter().getComponentIdentifier();
	}

	/**
	 *  Check if the corresponding plan was aborted because the
	 *  proprietary goal succeeded during the plan was running.
	 *  @return True, if the goal was aborted on success of the proprietary goal.
	 * /
	public boolean isAbortedOnSuccess()
	{
		return getRootGoal().isAbortedOnSuccess();
	}*/

	/**
	 *  Get the uncatched exception that occurred in the body (if any).
	 *  Method should only be called when in failed() method.
	 *  @return The exception.
	 */
	public Exception getException()
	{
		return (Exception)getState().getAttributeValue(getRPlan(), OAVBDIRuntimeModel.plan_has_exception);
	} 

	/**
	 *  Kill this agent.
	 */
	public void killAgent()
	{
//		capability.killAgent();
		// Problem: duplicate functionality here and in capability flyweight :-(
//		state.setAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state, 
//			OAVBDIRuntimeModel.AGENTLIFECYCLESTATE_TERMINATING);
		interpreter.startMonitorConsequences();
		getInterpreter().killComponent();
		interpreter.endMonitorConsequences();
	}

	//-------- capability shortcut methods --------
	
	/**
	 *  Get the belief base.
	 *  @return The belief base.
	 */
	public IBeliefbase getBeliefbase()
	{
		return BeliefbaseFlyweight.getBeliefbaseFlyweight(state, rcapa);
	}

	/**
	 *  Get the goal base.
	 *  @return The goal base.
	 */
	public IGoalbase getGoalbase()
	{
		return GoalbaseFlyweight.getGoalbaseFlyweight(state, rcapa);
	}

	/**
	 *  Get the plan base.
	 *  @return The plan base.
	 */
	public IPlanbase getPlanbase()
	{
		return PlanbaseFlyweight.getPlanbaseFlyweight(state, rcapa);
	}

	/**
	 *  Get the event base.
	 *  @return The event base.
	 */
	public IEventbase getEventbase()
	{
		return EventbaseFlyweight.getEventbaseFlyweight(state, rcapa);
	}

	/**
	 * Get the expression base.
	 * @return The expression base.
	 */
	public IExpressionbase getExpressionbase()
	{
		return ExpressionbaseFlyweight.getExpressionbaseFlyweight(state, rcapa);
	}
	
//	/**
//	 *  Get the property base.
//	 *  @return The property base.
//	 */
//	public IPropertybase getPropertybase()
//	{
//		return PropertybaseFlyweight.getPropertybaseFlyweight(state, rcapa);
//	}

	/**
	 *  Get the clock.
	 *  @return The clock.
	 */
	public IClockService getClock()
	{
		return interpreter.getClockService();
//		return (IClockService)interpreter.getServiceProvider().getService(IClockService.class);
	}

	/**
	 *  Get the current time.
	 *  The time unit depends on the currently running clock implementation.
	 *  For the default system clock, the time value adheres to the time
	 *  representation as used by {@link System#currentTimeMillis()}, i.e.,
	 *  the value of milliseconds passed since 0:00 'o clock, January 1st, 1970, UTC.
	 *  For custom simulation clocks, arbitrary representations can be used.
	 *  @return The current time.
	 */
	public long getTime()
	{
		return getClock().getTime();
	}

	//-------- goalbase shortcut methods --------
	
	/**
	 *  Dispatch a new top-level goal.
	 *  @param goal The new goal.
	 *  Note: plan step is interrupted after call.
	 */
	public void dispatchTopLevelGoal(IGoal goal)
	{
		Object rgoal = ((GoalFlyweight)goal).getHandle();
		interpreter.startMonitorConsequences();
		GoalLifecycleRules.adoptGoal(state, ((GoalFlyweight)goal).getScope(), rgoal);
		interpreter.endMonitorConsequences();
	}

	/**
	 *  Create a goal from a template goal.
	 *  To be processed, the goal has to be dispatched as subgoal
	 *  or adopted as top-level goal.
	 *  @param type	The template goal name as specified in the ADF.
	 *  @return The created goal.
	 */
	public IGoal createGoal(String type)
	{
		return (IGoal)SFlyweightFunctionality.createGoal(state, rcapa, type);
	}

	//-------- eventbase shortcut methods --------
	
	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public IFuture	sendMessage(IMessageEvent me)
	{	
		return sendMessage(me, null);
	}
	
	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public IFuture	sendMessage(IMessageEvent me, byte[] codecids)
	{	
		Object revent = ((MessageEventFlyweight)me).getHandle();
		Object rcapa = ((MessageEventFlyweight)me).getScope();
		interpreter.startMonitorConsequences();
		IFuture	ret	= MessageEventRules.sendMessage(state, rcapa, revent, codecids);
		interpreter.endMonitorConsequences();
		return ret;
	}

	/**
	 *  Dispatch an internal event.
	 *  @param event The event.
	 *  Note: plan step is interrupted after call.
	 */
	public void dispatchInternalEvent(IInternalEvent event)
	{
		Object revent = ((InternalEventFlyweight)event).getHandle();
		Object rcapa = ((InternalEventFlyweight)event).getScope();
		interpreter.startMonitorConsequences();
		InternalEventRules.adoptInternalEvent(state, rcapa, revent);
		interpreter.endMonitorConsequences();
	}

	/**
	 *  Create a new message event.
	 *  @return The new message event.
	 */
	public IMessageEvent createMessageEvent(String type)
	{
		return (IMessageEvent)SFlyweightFunctionality.createMessageEvent(state, rcapa, type);
	}

	/**
	 *  Create a new intenal event.
	 *  @return The new intenal event.
	 */
	public IInternalEvent createInternalEvent(String type)
	{
		return (IInternalEvent)SFlyweightFunctionality.createInternalEvent(state, rcapa, type);
	}

	/**
	 *  Create a new intenal event.
	 *  @return The new intenal event.
	 *  @deprecated Convenience method for easy conversion to new explicit internal events.
	 *  Will be removed in later releases.
	 * /
	public IInternalEvent createInternalEvent(String type, Object content)
	{
		return capability.getEventbase().createInternalEvent(type, content);
	}*/

	//-------- gui methods --------

	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	protected IExternalAccess createExternalAccess()
	{
		return new ExternalAccessFlyweight(state, rcapa);
	}

	//-------- expressionbase shortcut methods --------
	// Hack!!! Not really shortcuts, because expressions/conditions are remembered for cleanup.
	
	/**
	 *  Get a query created from a predefined expression.
	 *  @param name	The name of an expression defined in the ADF.
	 *  @return The query object.
	 *  @deprecated	Use @link{#getExpression(String)} instead.
	 * /
	public IExpression	getQuery(String name)
	{
		return	getExpression(name);
	}*/

	/**
	 *  Get an instance of a predefined expression.
	 *  @param name	The name of an expression defined in the ADF.
	 *  @return The expression instance.
	 */
	public IExpression	getExpression(String name)
	{
		Object mcapa = state.getAttributeValue(rcapa, OAVBDIRuntimeModel.element_has_model);
		Object mexp = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_expressions, name);
		if(mexp==null)
			throw new RuntimeException("Unknown expression: "+name);
		return ExpressionFlyweight.getExpressionFlyweight(state, rcapa, mexp);
	}

	/**
	 *  Get a condition predefined in the ADF.
	 *  Note that a new condition instance is returned each time this method is called.
	 *  @param name	The name of a condition defined in the ADF.
	 *  @return The condition object.
	 * /
	public ICondition	getCondition(String name)
	{
		return capability.getExpressionbase().getCondition(name);
	}*/

	/**
	 *  Create a precompiled query.
	 *  @param query	The query string.
	 *  @return The precompiled query.
	 *  @deprecated	Use @link{#createExpression(String)} instead.
	 * /
	public IExpression	createQuery(String query)
	{
		return createExpression(query);
	}*/

	/**
	 *  Create a precompiled expression.
	 *  @param expression	The expression string.
	 *  @return The precompiled expression.
	 */
	public IExpression	createExpression(String expression)
	{
		return createExpression(expression, null, null);
	}

	/**
	 *  Create a precompiled expression.
	 *  @param expression	The expression string.
	 *  @return The precompiled expression.
	 */
	public IExpression	createExpression(String expression, String[] paramnames, Class[] paramtypes)
	{
		// Hack!!! Should be configurable.
		IExpressionParser	exp_parser	= new JavaCCExpressionParser();
		String[]	imports	= interpreter.getModel(rcapa).getAllImports();
		
		Map	params	= null;
		if(paramnames!=null)
		{
			params	= new HashMap();
			for(int i=0; i<paramnames.length; i++)
			{
				params.put(paramnames[i], state.getTypeModel().getJavaType(paramtypes[i]));
			}
		}
		
		IParsedExpression pex = exp_parser.parseExpression(expression, imports, params, Thread.currentThread().getContextClassLoader());
		return new ExpressionNoModel(state, rcapa, pex);
	}

	/**
	 *  Create a condition, that is triggered whenever the expression
	 *  value changes to true.
	 *  @param expression	The condition expression.
	 *  @return The condition.
	 * /
	public ICondition	createCondition(String expression)
	{
		return createCondition(expression, ICondition.TRIGGER_CHANGES_TO_TRUE, null, null);
	}*/

	/**
	 *  Create a condition.
	 *  @param expression	The condition expression.
	 *  @param trigger	The condition trigger.
	 *  @return The condition.
	 * /
	public ICondition	createCondition(String expression, String trigger, String[] paramnames, Class[] paramtypes)
	{
		return capability.getExpressionbase().createCondition(expression, trigger, paramnames, paramtypes);
	}*/

	//-------- parameter handling --------

	/**
	 *  Get all parameters.
	 *  @return All parameters.
	 */
	public IParameter[]	getParameters()
	{
		IParameter[] ret;
		
		Collection params = getState().getAttributeValues(getRPlan(), 
			OAVBDIRuntimeModel.parameterelement_has_parameters);
		if(params!=null)
		{
			ret = new IParameter[params.size()];
			int i=0;
			for(Iterator it=params.iterator(); it.hasNext(); i++)
			{
				Object param = it.next();
				String name = (String)getState().getAttributeValue(param, OAVBDIMetaModel.modelelement_has_name);
				ret[i] = ParameterFlyweight.getParameterFlyweight(getState(), getScope(), param, name, getRPlan());
			}
		}
		else
		{
			ret = new IParameter[0];
		}
		return ret;
	}

	/**
	 *  Get all parameter sets.
	 *  @return All parameter sets.
	 */
	public IParameterSet[]	getParameterSets()
	{
		IParameterSet[] ret;
		
		Collection paramsets = getState().getAttributeValues(getRPlan(), 
			OAVBDIRuntimeModel.parameterelement_has_parametersets);
		if(paramsets!=null)
		{
			ret = new IParameterSet[paramsets.size()];
			int i=0;
			for(Iterator it=paramsets.iterator(); it.hasNext(); i++)
			{
				Object paramset = it.next();
				String name = (String)getState().getAttributeValue(paramset, OAVBDIMetaModel.modelelement_has_name);
				ret[i] = ParameterSetFlyweight.getParameterSetFlyweight(getState(), getScope(), paramset, name, getRPlan());
			}
		}
		else
		{
			ret = new IParameterSet[0];
		}
		return ret;
	}

	/**
	 *  Get a parameter.
	 *  @param name The name.
	 *  @return The parameter.
	 */
	public IParameter getParameter(String name)
	{
		return ParameterFlyweight.getParameterFlyweight(state, rcapa, null, name, rplan);
	}

	/**
	 *  Get a parameter.
	 *  @param name The name.
	 *  @return The parameter set.
	 */
	public IParameterSet getParameterSet(String name)
	{
		return ParameterSetFlyweight.getParameterSetFlyweight(state, rcapa, null, name, rplan);
	}

	/**
	 *  Has the element a parameter element.
	 *  @param name The name.
	 *  @return True, if it has the parameter.
	 */
	public boolean hasParameter(String name)
	{
		boolean ret = state.containsKey(rplan, OAVBDIRuntimeModel.parameterelement_has_parameters, name);
		if(!ret)
		{
			Object mplan = state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
			ret = state.containsKey(mplan, OAVBDIMetaModel.parameterelement_has_parameters, name);
		}
		return ret;
	}

	/**
	 *  Has the element a parameter set element.
	 *  @param name The name.
	 *  @return True, if it has the parameter set.
	 */
	public boolean hasParameterSet(String name)
	{
		boolean ret = state.containsKey(rplan, OAVBDIRuntimeModel.parameterelement_has_parametersets, name);
		if(!ret)
		{
			Object mplan = state.getAttributeValue(rplan, OAVBDIRuntimeModel.element_has_model);
			ret = state.containsKey(mplan, OAVBDIMetaModel.parameterelement_has_parametersets, name);
		}
		return ret;
	}

	//-------- internal methods --------

	/**
	 *  This method is called after the plan has been terminated.
	 *  It can be overriden to perform any custom cleanup code
	 *  but this implementation should be called also, because
	 *  it performs cleanup concerning expressions and conditions.
	 * /
	// Replaced by passed(), failed(), aborted().
	protected void cleanup()
	{
		// Cleanup expressions / conditions.
		/*for(int i=0; i<expressions.size(); i++)
		{
			// Resolve references to cleanup original expression.
			IRElement	exp	= (IRElement)expressions.get(i);
			while(exp instanceof RElementReference)
				exp	= ((RElementReference)exp).getReferencedElement();

			exp.cleanup();
		}* /
	}*/

	/**
	 *  Get the state.
	 *  @return The state.
	 */
	public IOAVState getState()
	{
		return state;
	}
	
	/**
	 *  Get the state.
	 *  @return The state.
	 */
	// todo: make package access
	public BDIInterpreter getInterpreter()
	{
		return interpreter;
	}
	
	/**
	 *  Get the event parent.
	 *  @return The event parent.
	 */
	public String getEventParent()
	{
		return getRPlan().toString();
	}
	
	/**
	 *  Get the plan instance info.
	 *  @return The plan instance info.
	 */
	// todo: make package access
	public Object getRPlan()
	{
		return rplan;
	}

	/**
	 *  Get the capability.
	 *  @return The capability.
	 */
	protected Object getRCapability()
	{
		return rcapa;
	}

	/**
	 *  Get the capability.
	 *  @return The capability.
	 * /
	protected CapabilityWrapper getCapability()
	{
		return capability;
	}*/
	
	//-------- listeners --------
	
	/**
	 *  Add a plan listener.
	 *  @param listener The plan listener.
	 */
	public void addPlanListener(IPlanListener listener)
	{
//		getInterpreter().getEventDispatcher().addPlanListener(getRPlan(), listener);
		IPlan plan = PlanFlyweight.getPlanFlyweight(getState(), getRCapability(), getRPlan());
		plan.addPlanListener(listener);
	}
	
	/**
	 *  Remove a plan listener.
	 *  @param listener The plan listener.
	 */
	public void removePlanListener(final IPlanListener listener)
	{
//		getInterpreter().getEventDispatcher().removePlanListener(getRPlan(), listener);
		IPlan plan = PlanFlyweight.getPlanFlyweight(getState(), getRCapability(), getRPlan());
		plan.removePlanListener(listener);
	}

	/**
	 *  Get the pool corresponding to the current plan lifecycle state.
	 *  @param steptype	The step type.
	 *  @return	The corresponding lane.
	 */
	protected String getPool(String steptype)
	{
		String	pool	= null;
		List	pools	= getModelElement().getPools();
		if(pools!=null && !pools.isEmpty())
		{
			for(int i=0; pool==null && i<pools.size(); i++)
			{
				String name	= ((MPool)pools.get(i)).getName();
				if(name.trim().toLowerCase().equals(steptype))
					pool	= name;
			}
			
			if(pool==null)
				pool	= POOL_UNDEFINED;
		}
		
		if(pool==null && !OAVBDIRuntimeModel.PLANLIFECYCLESTATE_BODY.equals(steptype))
			pool	= POOL_UNDEFINED;

		return pool;
	}

	Collection	cts;
	Collection	ecs;
	Collection	fas;
	Collection	fcs;
	Collection	frs;
	Collection	gfs;
	Collection	gls;
	Collection	its;
	Collection	mes;
	Collection	mts;

	/**
	 *  Update the waitqueue wait abstraction, but keep user entries, if any.
	 *  @param wa	The new wait abstraction.
	 */
	protected void updateWaitqueue(Object wa)
	{
		// Retain old user settings.
		Collection	oldcts	= Collections.EMPTY_SET;
		Collection	oldecs	= Collections.EMPTY_SET;
		Collection	oldfas	= Collections.EMPTY_SET;
		Collection	oldfcs	= Collections.EMPTY_SET;
		Collection	oldfrs	= Collections.EMPTY_SET;
		Collection	oldgfs	= Collections.EMPTY_SET;
		Collection	oldgls	= Collections.EMPTY_SET;
		Collection	oldits	= Collections.EMPTY_SET;
		Collection	oldmes	= Collections.EMPTY_SET;
		Collection	oldmts	= Collections.EMPTY_SET;
		Object	oldwa	= state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuewa);
		if(oldwa!=null)
		{
			oldcts	= state.getAttributeValues(oldwa, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes);
			oldecs	= state.getAttributeValues(oldwa, OAVBDIRuntimeModel.waitabstraction_has_externalconditions);
			oldfas	= state.getAttributeValues(oldwa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds);
			oldfcs	= state.getAttributeValues(oldwa, OAVBDIRuntimeModel.waitabstraction_has_factchangeds);
			oldfrs	= state.getAttributeValues(oldwa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds);
			oldgfs	= state.getAttributeValues(oldwa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds);
			oldgls	= state.getAttributeValues(oldwa, OAVBDIRuntimeModel.waitabstraction_has_goals);
			oldits	= state.getAttributeValues(oldwa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes);
			oldmes	= state.getAttributeValues(oldwa, OAVBDIRuntimeModel.waitabstraction_has_messageevents);
			oldmts	= state.getAttributeValues(oldwa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes);
			
			oldcts	= oldcts!=null ? new HashSet(oldcts) : Collections.EMPTY_SET; 
			oldecs	= oldecs!=null ? new HashSet(oldecs) : Collections.EMPTY_SET; 
			oldfas	= oldfas!=null ? new HashSet(oldfas) : Collections.EMPTY_SET; 
			oldfcs	= oldfcs!=null ? new HashSet(oldfcs) : Collections.EMPTY_SET; 
			oldfrs	= oldfrs!=null ? new HashSet(oldfrs) : Collections.EMPTY_SET; 
			oldgfs	= oldgfs!=null ? new HashSet(oldgfs) : Collections.EMPTY_SET; 
			oldgls	= oldgls!=null ? new HashSet(oldgls) : Collections.EMPTY_SET; 
			oldits	= oldits!=null ? new HashSet(oldits) : Collections.EMPTY_SET; 
			oldmes	= oldmes!=null ? new HashSet(oldmes) : Collections.EMPTY_SET; 
			oldmts	= oldmts!=null ? new HashSet(oldmts) : Collections.EMPTY_SET;
			
			if(cts!=null) oldcts.removeAll(cts);
			if(ecs!=null) oldecs.removeAll(ecs);
			if(fas!=null) oldfas.removeAll(fas);
			if(fcs!=null) oldfcs.removeAll(fcs);
			if(frs!=null) oldfrs.removeAll(frs);
			if(gfs!=null) oldgfs.removeAll(gfs);
			if(gls!=null) oldgls.removeAll(gls);
			if(its!=null) oldits.removeAll(its);
			if(mes!=null) oldmes.removeAll(mes);
			if(mts!=null) oldmts.removeAll(mts);
		}
		
		// Remember new non-user settings (for finding out user settings later). 
		if(wa!=null)
		{
			cts	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes);
			ecs	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_externalconditions);
			fas	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds);
			fcs	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factchangeds);
			frs	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds);
			gfs	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds);
			gls	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goals);
			its	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes);
			mes	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents);
			mts	= state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes);
			
			cts	= cts!=null ? new HashSet(cts) : Collections.EMPTY_SET; 
			ecs	= ecs!=null ? new HashSet(ecs) : Collections.EMPTY_SET; 
			fas	= fas!=null ? new HashSet(fas) : Collections.EMPTY_SET; 
			fcs	= fcs!=null ? new HashSet(fcs) : Collections.EMPTY_SET; 
			frs	= frs!=null ? new HashSet(frs) : Collections.EMPTY_SET; 
			gfs	= gfs!=null ? new HashSet(gfs) : Collections.EMPTY_SET; 
			gls	= gls!=null ? new HashSet(gls) : Collections.EMPTY_SET; 
			its	= its!=null ? new HashSet(its) : Collections.EMPTY_SET; 
			mes	= mes!=null ? new HashSet(mes) : Collections.EMPTY_SET; 
			mts	= mts!=null ? new HashSet(mts) : Collections.EMPTY_SET; 
		}
		
		// Re-add user settings
		if(!oldcts.isEmpty() || !oldecs.isEmpty() || !oldfas.isEmpty() || !oldfcs.isEmpty() || !oldfrs.isEmpty()
			|| !oldgfs.isEmpty() || !oldgls.isEmpty() || !oldits.isEmpty() || !oldmes.isEmpty() || !oldmts.isEmpty())
		{
			if(wa==null)
				wa	= state.createObject(OAVBDIRuntimeModel.waitabstraction_type);
			
			for(Iterator it=oldcts.iterator(); it.hasNext(); )
				state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes, it.next());
			for(Iterator it=oldecs.iterator(); it.hasNext(); )
				state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_externalconditions, it.next());
			for(Iterator it=oldfas.iterator(); it.hasNext(); )
				state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds, it.next());
			for(Iterator it=oldfcs.iterator(); it.hasNext(); )
				state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factchangeds, it.next());
			for(Iterator it=oldfrs.iterator(); it.hasNext(); )
				state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds, it.next());
			for(Iterator it=oldgfs.iterator(); it.hasNext(); )
				state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds, it.next());
			for(Iterator it=oldgls.iterator(); it.hasNext(); )
				state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_goals, it.next());
			for(Iterator it=oldits.iterator(); it.hasNext(); )
				state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes, it.next());
			for(Iterator it=oldmes.iterator(); it.hasNext(); )
				state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents, it.next());
			for(Iterator it=oldmts.iterator(); it.hasNext(); )
				state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes, it.next());
		}

		interpreter.getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuewa, wa);
	}

	/**
	 *  Delegate synchronization to agent.
	 * /
	public void invokeLater(final Runnable action)
	{
		// Redirect to component instance.
		interpreter.invokeLater(action);
		
		// Called from outside (e.g. workflow client)
		// when task is finished
		// Check if plan should be set to ready (Hack!!!)
//		interpreter.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				// The DefaultActivityHandler.step() may rethrow an exception that
//				// occurred within task execution.
//				// It is catched an stored in the plan to be rethrown from the
//				// BPMNPlanExecutor.executeStep() method.
//				// The plan must always be set to ready to transfer the state
//				// of the process thread to the plan.
//				try
//				{
//					action.run();
//				}
//				catch(Throwable t)
//				{
//					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_exception, t);
//				}
//				state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
//				
////				String lane = getLane(getLastState());
////				if(isReady(null, lane))
////				{
////					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
////				}
//			}	
//		});
	}
	
	/**
	 *  Method that should be called, when an activity is finished and the following activity should be scheduled.
	 *  Can safely be called from external threads.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param event	The event that has occurred, if any.
	 */
	public void	notify(final MActivity activity, final ProcessThread thread, final Object event)
	{
		if(isExternalThread())
		{
			getComponentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					if(isCurrentActivity(activity, thread))
					{
						try
						{
							step(activity, BpmnPlanBodyInstance.this, thread, event);
						}
						catch(Throwable t)
						{
							if(state.containsObject(rplan))
								state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_exception, t);
						}
						thread.setNonWaiting();
						if(state.containsObject(rplan))
						{
//							System.out.println("Ready: "+activity+" "+thread);
							state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
						}
					}
					else
					{
						System.out.println("Nop, due to outdated notify: "+thread+" "+activity);
					}
				}
			});
		}
		else
		{
			if(isCurrentActivity(activity, thread))
			{
				try
				{
					step(activity, BpmnPlanBodyInstance.this, thread, event);
				}
				catch(Throwable t)
				{
					if(state.containsObject(rplan))
						state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_exception, t);
				}
				thread.setNonWaiting();
				if(state.containsObject(rplan))
				{
//					System.out.println("Ready: "+activity+" "+thread);
					state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
				}
			}
			else
			{
				System.out.println("Nop, due to outdated notify: "+thread+" "+activity);
			}
		}
	}
	
//	/**
//	 *  Delegate synchronization to agent.
//	 */
//	public boolean isAgentThread()
//	{
//		return interpreter.isAgentThread();
//	}
	
	/**
	 *  Delegate synchronization to agent.
	 */
	public boolean isExternalThread()
	{
		return interpreter.isExternalThread();
	}
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 *  @return The result of the step.
	 */
	public IFuture scheduleStep(IComponentStep step)
	{
		return interpreter.scheduleStep(step, null);
	}
	
	//-------- IInternalAccess interface --------
	
	/**
	 *  Get the service provider.
	 *  @return The service provider.
	 */
	public IServiceProvider getServiceProvider()
	{
		return interpreter.getServiceProvider();
	}
	
//	/**
//	 *  Add an component listener.
//	 *  @param listener The listener.
//	 */
//	public IFuture addComponentListener(IComponentListener listener)
//	{
//		ElementFlyweight.addEventListener(listener, interpreter.getAgent(), getState(), interpreter.getAgent());
//		return IFuture.DONE;
//	}
//	
//	/**
//	 *  Remove a component listener.
//	 *  @param listener The listener.
//	 */
//	public IFuture removeComponentListener(IComponentListener listener)
//	{
//		ElementFlyweight.removeEventListener(listener, interpreter.getAgent(), false, getState(), interpreter.getAgent());
//		return IFuture.DONE;
//	}
	
	/**
	 *  Service container is set from the outside.
	 */
	public IServiceContainer createServiceContainer()
	{
		return null;
	}
}
