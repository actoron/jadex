package jadex.bdiv3x.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MCondition;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MInternalEvent;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IBeliefListener;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.WaitAbstraction;
import jadex.bdiv3.runtime.impl.BeliefAdapter;
import jadex.bdiv3.runtime.impl.BodyAborted;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3.runtime.impl.PlanAbortedException;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bdiv3.runtime.impl.RCapability;
import jadex.bdiv3.runtime.impl.RElement;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bdiv3x.BDIXModel;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.IParsedExpression;
import jadex.javaparser.SJavaParser;
import jadex.rules.eca.ChangeInfo;
import jadex.rules.eca.EventType;
import jadex.rules.eca.IAction;
import jadex.rules.eca.ICondition;
import jadex.rules.eca.IEvent;
import jadex.rules.eca.IRule;
import jadex.rules.eca.Rule;

/**
 *  Dummy class for loading v2 examples using v3x.
 */
@jadex.bdiv3.annotation.Plan
public abstract class Plan
{
	public Plan()
	{
		// TODO Auto-generated constructor stub
	}
	
	/** The internal access. */
	@PlanCapability
	protected IInternalAccess agent;
	
	/** The rplan. */
	@PlanAPI
	protected RPlan rplan;
	
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	@PlanBody
	public abstract void body();

	/**
	 *  The passed method is called on plan success.
	 */
	@PlanPassed
	public void	passed()
	{
	}

	/**
	 *  The failed method is called on plan failure/abort.
	 */
	@PlanFailed
	public void	failed()
	{
	}

	/**
	 *  The plan was aborted (because of conditional goal
	 *  success or termination from outside).
	 */
	@PlanAborted
	public void aborted()
	{
	}
	
	/**
	 *  Wait for a some time.
	 *  @param duration The duration.
	 */
	public void	waitFor(long timeout)
	{
		checkNotInAtomic();
		
		agent.getComponentFeature(IExecutionFeature.class).waitForDelay(timeout).get();
	}
	
	/**
	 *  Wait for next tick.
	 */
	public void	waitForTick()
	{
		checkNotInAtomic();
		
		agent.getComponentFeature(IExecutionFeature.class).waitForTick().get();
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
		return getGoalbase().createGoal(type);
	}
	
	/**
	 *  Dispatch a new top-level goal.
	 *  @param goal The new goal.
	 */
	public void	dispatchSubgoalAndWait(IGoal goal)
	{
		dispatchSubgoalAndWait(goal, -1);
	}
	
	/**
	 *  Dispatch a new top-level goal.
	 *  @param goal The new goal.
	 */
	public void	dispatchSubgoalAndWait(IGoal goal, long timeout)
	{
		checkNotInAtomic();
		
		dispatchSubgoal(goal);
		RGoal rgoal = (RGoal)goal;
		Future<Void> ret = new Future<Void>();
		rgoal.addListener(new DelegationResultListener<Void>(ret));
		try
		{
			ret.get(timeout);
		}
		catch(BodyAborted e)
		{
			throw new GoalFailureException(null, new PlanAbortedException());
		}
		catch(GoalFailureException e)
		{
			throw e;
		}
		catch(TimeoutException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new GoalFailureException(null, e);
		}
	}
	
	/**
	 *  Wait for a goal.
	 *  @param type The goal type.
	 */
	public IGoal waitForGoal(String type)
	{
		return waitForGoal(type, -1);
	}

	/**
	 *  Wait for a goal.
	 *  @param type The goal type.
	 *  @param timeout The timeout.
	 */
	public IGoal waitForGoal(String type, long timeout)
	{
		checkNotInAtomic();
		
		final Future<IGoal> ret = new Future<IGoal>();
		
		BDIXModel model = (BDIXModel)agent.getModel().getRawModel();
		// todo: add capability name of scope
		MGoal mgoal = model.getCapability().getGoal(type.replace(".", MElement.CAPABILITY_SEPARATOR));
		if(mgoal==null)
			throw new RuntimeException("Unknown goal type: "+mgoal);
		WaitAbstraction wa = new WaitAbstraction();
		wa.addModelElement(mgoal);
		

		IGoal res = (IGoal)rplan.getFromWaitqueue(wa);
		if(res!=null)
		{
			return res;
		}
		else
		{
			// todo: add scope name if is in capa
			rplan.setWaitAbstraction(wa);
			return ret.get(timeout);
		}
	}
		
	/**
	 *  Wait for a goal to be finished.
	 *  @param goal The goal.
	 */
	public void waitForGoal(IGoal goal)
	{
		waitForGoal(goal, -1);
	}
	
	/**
	 *  Wait for a goal to be finished.
	 *  @param goal The goal.
	 */
	public void waitForGoal(IGoal goal, long timeout)
	{
		checkNotInAtomic();
		if(goal==null)
			throw new IllegalArgumentException("Goal must not null.");
				
		RGoal rgoal = (RGoal)goal;
		Future<Void> ret = new Future<Void>();
		rgoal.addListener(new DelegationResultListener<Void>(ret));
		try
		{
			ret.get(timeout);
		}
//		catch(BodyAborted e)
//		{
//			throw new GoalFailureException(null, new PlanAbortedException());
//		}
		catch(GoalFailureException e)
		{
			throw e;
		}
		catch(TimeoutException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new GoalFailureException(null, e);
		}
	}
	
	/**
	 *  Wait for a message event.
	 *  @param type The message event type.
	 */
	public IMessageEvent waitForMessageEvent(String type)
	{
		return waitForMessageEvent(type, -1);
	}

	/**
	 *  Wait for a message event.
	 *  @param type The message event type.
	 *  @param timeout The timeout.
	 */
	public IMessageEvent waitForMessageEvent(String type, long timeout)
	{
		checkNotInAtomic();
		
		final Future<IMessageEvent> ret = new Future<IMessageEvent>();

		IInternalBDIAgentFeature bdif = agent.getComponentFeature(IInternalBDIAgentFeature.class);
		MMessageEvent mevent = bdif.getBDIModel().getCapability().getMessageEvent(type);
		WaitAbstraction wa = new WaitAbstraction();
		wa.addModelElement(mevent);

		IMessageEvent res = (IMessageEvent)rplan.getFromWaitqueue(wa);
		if(res!=null)
		{
			return res;
		}
		else
		{
			// todo: add scope name if is in capa
			rplan.setWaitAbstraction(wa);
			
			// todo: timeout
			
	//		final ResumeCommand<IMessageEvent> rescom = getRPlan().new ResumeCommand<IMessageEvent>(ret, false);
	//
	//		if(timeout>-1)
	//		{
	//			IFuture<ITimer> cont = getRPlan().createTimer(timeout, agent, rescom);
	//			cont.addResultListener(new DefaultResultListener<ITimer>()
	//			{
	//				public void resultAvailable(final ITimer timer)
	//				{
	//					if(timer!=null)
	//						rescom.setTimer(timer);
	//				}
	//			});
	//		}
			
	//		rplan.addResumeCommand(rescom);
			
			return ret.get(timeout);
		}
	}
	
	/**
	 *  Wait for a reply to a message event.
	 * 	@param event The message event.
	 *  @param type The reply.
	 */
	public IMessageEvent waitForReply(IMessageEvent event)
	{
		return waitForReply(event, -1);
	}

	/**
	 *  Wait for a reply to a message event.
	 *  @param event The message event.
	 *  @param timeout The timeout.
	 */
	public IMessageEvent waitForReply(IMessageEvent event, long timeout)
	{
		checkNotInAtomic();
		
		Future<IMessageEvent> ret = new Future<IMessageEvent>();
		WaitAbstraction wa = new WaitAbstraction();
		wa.addReply((RMessageEvent)event);

		IMessageEvent res = (IMessageEvent)rplan.getFromWaitqueue(wa);
		if(res!=null)
		{
			return res;
		}
		else
		{
			// todo: add scope name if is in capa
			rplan.setWaitAbstraction(wa);
			return ret.get(timeout);
		}
	}
	
	/**
	 *  Wait for an internal event.
	 *  @param type The internal event type.
	 */
	public IInternalEvent waitForInternalEvent(String type)
	{
		return waitForInternalEvent(type, -1);
	}

	/**
	 *  Wait for an internal event.
	 *  @param type The internal event type.
	 *  @param timeout The timeout.
	 */
	public IInternalEvent waitForInternalEvent(String type, long timeout)
	{
		checkNotInAtomic();
		
		final Future<IInternalEvent> ret = new Future<IInternalEvent>();

		IInternalBDIAgentFeature bdif = agent.getComponentFeature(IInternalBDIAgentFeature.class);
		MInternalEvent mevent = bdif.getBDIModel().getCapability().getInternalEvent(type);
		WaitAbstraction wa = new WaitAbstraction();
		wa.addModelElement(mevent);

		IInternalEvent res = (IInternalEvent)rplan.getFromWaitqueue(wa);
		if(res!=null)
		{
			return res;
		}
		else
		{
			// todo: add scope name if is in capa
			rplan.setWaitAbstraction(wa);
			
			// todo: timeout
			
	//		final ResumeCommand<IMessageEvent> rescom = getRPlan().new ResumeCommand<IMessageEvent>(ret, false);
	//
	//		if(timeout>-1)
	//		{
	//			IFuture<ITimer> cont = getRPlan().createTimer(timeout, agent, rescom);
	//			cont.addResultListener(new DefaultResultListener<ITimer>()
	//			{
	//				public void resultAvailable(final ITimer timer)
	//				{
	//					if(timer!=null)
	//						rescom.setTimer(timer);
	//				}
	//			});
	//		}
			
	//		rplan.addResumeCommand(rescom);
			
			return ret.get(timeout);
		}
	}
	
	/**
	 *  Kill this agent.
	 */
	public void	killAgent()
	{
		agent.killComponent();
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return agent.getLogger();
	}
	
	/**
	 *  Get the beliefbase.
	 *  @return The beliefbase.
	 */
	public IBeliefbase getBeliefbase()
	{
		return getCapability().getBeliefbase();
	}

	/**
	 *  Get the rplan.
	 *  @return The rplan
	 */
	public IPlan getRPlan()
	{
		return rplan;
	}
	
	/**
	 *  Get the reason this plan was created for.
	 *  @return The reason.
	 */
	public Object getReason()
	{
		return rplan.getReason();
	}
	
	/**
	 *  Get the reason this plan was created for.
	 *  @return The reason.
	 */
	public Object getDispatchedElement()
	{
		return rplan.getDispatchedElement();
	}
	
	/**
	 *  Get a parameter.
	 *  @param name The name.
	 *  @return The parameter.
	 */
	public IParameter getParameter(String name)
	{
		return rplan.getParameter(name);
	}
	
	/**
	 *  Get a parameter.
	 *  @param name The name.
	 *  @return The parameter.
	 */
	public IParameterSet getParameterSet(String name)
	{
		return rplan.getParameterSet(name);
	}
	
	/**
	 *  Get an expression by name.
	 *  @name The expression name.
	 *  @return The expression.
	 */
	public IExpression getExpression(String name)
	{
		return getExpressionbase().getExpression(name);
	}
	
	/**
	 *  Get an expression by name.
	 *  @name The expression name.
	 *  @return The expression.
	 */
	public IExpression createExpression(String exp)
	{
		return getExpressionbase().createExpression(exp);
	}
	
	/**
	 *  Send a message and wait for the answer.
	 *  @param me The message event.
	 *  @return The result event.
	 */
	public IMessageEvent sendMessageAndWait(IMessageEvent me)
	{
		return sendMessageAndWait(me, -1);
	}

	/**
	 *  Send a message and wait for the answer.
	 *  Adds a reply_with entry if not present, for tracking the conversation.
	 *  @param me The message event.
	 *  @param timeout The timeout.
	 *  @return The result event.
	 */
	public IMessageEvent sendMessageAndWait(IMessageEvent me, long timeout)
	{
		checkNotInAtomic();
		
		IInternalBDIAgentFeature bdif = agent.getComponentFeature(IInternalBDIAgentFeature.class);
		WaitAbstraction wa = new WaitAbstraction();
		wa.addReply((RMessageEvent)me);

		rplan.setWaitAbstraction(wa);
		
		sendMessage(me);
		
		Future<IMessageEvent> ret = new Future<IMessageEvent>();
		return ret.get(timeout);
	}

	/**
	 *  Send a message and wait until it is sent.
	 *  @param me	The message event.
	 */
	public IFuture<Void> sendMessage(IMessageEvent me)
	{
		return getEventbase().sendMessage(me);
	}
	
	/**
	 *  Let the plan fail.
	 */
	public void fail()
	{
		throw new PlanFailureException();
	}
	
	/**
	 *  Get the capability.
	 *  @return The capability.
	 */
	protected RCapability getCapability()
	{
		return agent.getComponentFeature(IInternalBDIAgentFeature.class).getCapability();
	}
	
	//-------- legacy --------
	

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
		return new jadex.bdiv3x.runtime.RCapability(agent);
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
		rplan.setAtomic(true);
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
		rplan.setAtomic(false);
		
		// Process events after atomic or is at the end of a step enough?
//		IInternalBDIAgentFeature bdif = agent.getComponentFeature(IInternalBDIAgentFeature.class);
//		bdif.getRuleSystem().processAllEvents();
	}

	/**
	 *  Dispatch a new subgoal.
	 *  @param subgoal The new subgoal.
	 *  Note: plan step is interrupted after call.
	 */
	public IFuture<Void> dispatchSubgoal(IGoal subgoal)
	{
		if(subgoal==null)
			throw new IllegalArgumentException("Subgoal must not null.");
		
		Future<Void> ret = new Future<Void>();
		RGoal rgoal = (RGoal)subgoal;
		rgoal.setParent(rplan);
		rplan.addSubgoal(rgoal);
		RGoal.adoptGoal(rgoal, agent);
		rgoal.addListener(new DelegationResultListener<Void>(ret));
		return ret;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(SReflect.getInnerClassName(this.getClass()));
		buf.append("(");
		buf.append(rplan);
		buf.append(")");
		return buf.toString();
	}

	/**
	 *  Get the agent name.
	 *  @return The agent name.
	 */
	public String getComponentName()
	{
		return getComponentIdentifier().getLocalName();
	}
	
	/**
	 * Get the agent identifier.
	 * @return The agent identifier.
	 */
	public IComponentIdentifier	getComponentIdentifier()
	{
		return agent.getComponentIdentifier();
	}
	
	/**
	 * Get the agent description.
	 * @return The agent description.
	 */
	public IComponentDescription getComponentDescription()
	{
		return agent.getComponentDescription();
	}

	/**
	 *  Get the uncatched exception that occurred in the body (if any).
	 *  Method should only be called when in failed() method.
	 *  @return The exception.
	 */
	public Exception getException()
	{
		return getRPlan().getException();
	} 

	/**
	 *  Get the goal base.
	 *  @return The goal base.
	 */
	public IGoalbase getGoalbase()
	{
		return getCapability().getGoalbase();
	}

	/**
	 *  Get the plan base.
	 *  @return The plan base.
	 */
	public IPlanbase getPlanbase()
	{
		return getCapability().getPlanbase();
	}

	/**
	 *  Get the event base.
	 *  @return The event base.
	 */
	public IEventbase getEventbase()
	{
		return getCapability().getEventbase();
	}

	/**
	 * Get the expression base.
	 * @return The expression base.
	 */
	public IExpressionbase getExpressionbase()
	{
		return getCapability().getExpressionbase();
	}
	
	/**
	 *  Get the clock.
	 *  @return The clock.
	 */
	public IClockService getClock()
	{
		return SServiceProvider.getLocalService(agent, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM);
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
	
	/**
	 *  Dispatch a new top-level goal.
	 *  @param goal The new goal.
	 *  Note: plan step is interrupted after call.
	 */
	public void dispatchTopLevelGoal(IGoal goal)
	{
		getGoalbase().dispatchTopLevelGoal(goal);
	}

//	/**
//	 *  Send a message.
//	 *  @param me	The message event.
//	 */
//	public IFuture<Void> sendMessage(IMessageEvent me, byte[] codecids)
//	{	
//		return getEventbase().sendMessage(me, codecids);
//	}

	/**
	 *  Dispatch an internal event.
	 *  @param event The event.
	 *  Note: plan step is interrupted after call.
	 */
	public void dispatchInternalEvent(IInternalEvent event)
	{
		getEventbase().dispatchInternalEvent(event);
	}

	/**
	 *  Create a new message event.
	 *  @return The new message event.
	 */
	public IMessageEvent createMessageEvent(String type)
	{
		return getEventbase().createMessageEvent(type);
	}

	/**
	 *  Create a new intenal event.
	 *  @return The new intenal event.
	 */
	public IInternalEvent createInternalEvent(String type)
	{
		return getEventbase().createInternalEvent(type);
	}

	/**
	 *  Get the scope.
	 *  @return The scope.
	 */
	public IExternalAccess getExternalAccess()
	{
		return agent.getExternalAccess();
	}

//	/**
//	 *  Create a precompiled expression.
//	 *  @param expression	The expression string.
//	 *  @return The precompiled expression.
//	 */
//	public IExpression	createExpression(String expression, String[] paramnames, Class<?>[] paramtypes)
//	{
//		throw new UnsupportedOperationException();
//	}

	/**
	 *  Get all parameters.
	 *  @return All parameters.
	 */
	public IParameter[]	getParameters()
	{
		return rplan.getParameters();
	}

	/**
	 *  Get all parameter sets.
	 *  @return All parameter sets.
	 */
	public IParameterSet[]	getParameterSets()
	{
		return rplan.getParameterSets();
	}

	/**
	 *  Has the element a parameter element.
	 *  @param name The name.
	 *  @return True, if it has the parameter.
	 */
	public boolean hasParameter(String name)
	{
		return rplan.hasParameter(name);
	}

	/**
	 *  Has the element a parameter set element.
	 *  @param name The name.
	 *  @return True, if it has the parameter set.
	 */
	public boolean hasParameterSet(String name)
	{
		return rplan.hasParameterSet(name);
	}

	/**
	 *  Get the agent.
	 *  @return The agent
	 */
	public IInternalAccess getAgent()
	{
		return agent;
	}
	
//	/**
//	 *  Get the waitqueue.
//	 */
//	public List<Object> getWaitqueue()
//	{
//		return rplan.getWaitqueue();
//	}
	
	/**
	 *  Get the waitqueue.
	 */
	public PlanWaitAbstraction getWaitqueue()
	{
		return new PlanWaitAbstraction();
	}
	
	/**
	 *  Wait for a fact change of a belief.
	 */
	public Object waitForFactChanged(String belname)
	{
		return waitForFactChanged(belname, -1);
	}
	
	/**
	 *  Wait for a fact change of a belief.
	 */
	public Object waitForFactChanged(String belname, long timeout)
	{
		checkNotInAtomic();
		
		IInternalBDIAgentFeature bdif = agent.getComponentFeature(IInternalBDIAgentFeature.class);
		WaitAbstraction wa = new WaitAbstraction();
		wa.addChangeEventType(ChangeEvent.FACTCHANGED+"."+belname);

		ChangeEvent res = (ChangeEvent)rplan.getFromWaitqueue(wa);
		if(res!=null)
		{
			return res.getValue();
		}
		else
		{
			final Future<Object> ret = new Future<Object>();
//			IInternalBDIAgentFeature bdif = agent.getComponentFeature(IInternalBDIAgentFeature.class);
			IBeliefListener<Object> lis = new BeliefAdapter<Object>()
			{
				public void beliefChanged(ChangeInfo<Object> info)
				{
					ret.setResultIfUndone(info.getValue());
				}
			};
			bdif.addBeliefListener(belname, lis);
			try
			{
				return ret.get(timeout);
			}
			finally
			{
				bdif.removeBeliefListener(belname, lis);
			}
		}
	}
	
	/**
	 *  Wait for a fact added.
	 */
	public Object waitForFactAdded(String belname)
	{
		return waitForFactAdded(belname, -1);
	}
	
	/**
	 *  Wait for a fact added.
	 */
	public Object waitForFactAdded(String belname, long timeout)
	{
		checkNotInAtomic();
		
		IInternalBDIAgentFeature bdif = agent.getComponentFeature(IInternalBDIAgentFeature.class);
		WaitAbstraction wa = new WaitAbstraction();
		wa.addChangeEventType(ChangeEvent.FACTADDED+"."+belname);

		ChangeEvent res = (ChangeEvent)rplan.getFromWaitqueue(wa);
		if(res!=null)
		{
			return res.getValue();
		}
		else
		{
			final Future<Object> ret = new Future<Object>();
			IBeliefListener<Object> lis = new BeliefAdapter<Object>()
			{
				public void factAdded(ChangeInfo<Object> info)
				{
					ret.setResultIfUndone(info.getValue());
				}
			};
			((IBDIXAgentFeature)bdif).getBeliefbase().getBeliefSet(belname).addBeliefSetListener(lis);
			try
			{
				return ret.get(timeout);
			}
			finally
			{
				((IBDIXAgentFeature)bdif).getBeliefbase().getBeliefSet(belname).removeBeliefSetListener(lis);
			}
		}
	}
	
	/**
	 *  Wait for a fact added.
	 */
	public Object waitForFactRemoved(String belname)
	{
		return waitForFactRemoved(belname, -1);
	}
	
	/**
	 *  Wait for a fact added.
	 */
	public Object waitForFactRemoved(String belname, long timeout)
	{
		checkNotInAtomic();
		
		IInternalBDIAgentFeature bdif = agent.getComponentFeature(IInternalBDIAgentFeature.class);
		WaitAbstraction wa = new WaitAbstraction();
		wa.addChangeEventType(ChangeEvent.FACTREMOVED+"."+belname);

		ChangeEvent res = (ChangeEvent)rplan.getFromWaitqueue(wa);
		if(res!=null)
		{
			return res.getValue();
		}
		else
		{
			final Future<Void> ret = new Future<Void>();
			IBeliefListener<Object> lis = new BeliefAdapter<Object>()
			{
				public void factRemoved(ChangeInfo<Object> info)
				{
					ret.setResultIfUndone(null);
				}
			};
			((IBDIXAgentFeature)bdif).getBeliefbase().getBeliefSet(belname).addBeliefSetListener(lis);
			try
			{
				return ret.get(timeout);
			}
			finally
			{
				((IBDIXAgentFeature)bdif).getBeliefbase().getBeliefSet(belname).removeBeliefSetListener(lis);
			}
		}
	}
	
	/**
	 *  Wait for a condition.
	 *  @param name The name of the condition.
	 */
	public void waitForCondition(String name)
	{
		waitForCondition(name, -1);
	}
	
	/**
	 *  Wait for a condition.
	 *  @param name The name of the condition.
	 */
	public void waitForCondition(String name, long timeout)
	{
		checkNotInAtomic();
		
		final IInternalBDIAgentFeature bdif = agent.getComponentFeature(IInternalBDIAgentFeature.class);
		final MCondition mcond = bdif.getCapability().getMCapability().getCondition(name);
		if(mcond==null)
			throw new RuntimeException("Unknown condition: "+name);
		final Future<Void> ret = new Future<Void>();
		Rule<Void> rule = new Rule<Void>("plan_condition_"+rplan.getId()+"_"+mcond.getName(), new ICondition()
		{
			public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
			{
				UnparsedExpression uexp = mcond.getExpression();
				Boolean ret = (Boolean)SJavaParser.parseExpression(uexp, getAgent().getModel().getAllImports(), 
					getAgent().getClassLoader()).getValue(getAgent().getFetcher());
				return new Future<Tuple2<Boolean, Object>>(ret!=null && ret.booleanValue()? TRUE: FALSE);
			}
		}, new IAction<Void>()
		{
			public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
			{
				// Remove rule and continue after wait
				bdif.getRuleSystem().getRulebase().removeRule(rule.getName());
				ret.setResult(null);
				return IFuture.DONE;
			}
		});
		rule.setEvents(mcond.getEvents());
		bdif.getRuleSystem().getRulebase().addRule(rule);
		ret.get(timeout);
	}
	
	/**
	 *  Wait for a condition.
	 *  @param name The name of the condition.
	 */
	public void waitForConditionInline(String expr)
	{
		waitForConditionInline(expr, -1);
	}
	
	/**
	 *  Wait for a condition.
	 *  @param name The name of the condition.
	 */
	public void waitForConditionInline(final String expr, long timeout)
	{
		checkNotInAtomic();
		
		final UnparsedExpression uexp = new UnparsedExpression(null, expr);
		final IParsedExpression exp = SJavaParser.parseExpression(uexp, getAgent().getModel().getAllImports(), 
			getAgent().getClassLoader());
		List<EventType> events = new ArrayList<EventType>();
		BDIAgentFeature.addExpressionEvents(uexp, events, rplan.getModelElement());
		
		final IInternalBDIAgentFeature bdif = agent.getComponentFeature(IInternalBDIAgentFeature.class);
		final Future<Void> ret = new Future<Void>();
		Rule<Void> rule = new Rule<Void>("plan_condition_"+rplan.getId()+"_"+expr, new ICondition()
		{
			public IFuture<Tuple2<Boolean, Object>> evaluate(IEvent event)
			{
				Boolean ret = (Boolean)exp.getValue(getAgent().getFetcher());				
				return new Future<Tuple2<Boolean, Object>>(ret!=null && ret.booleanValue()? TRUE: FALSE);
			}
		}, new IAction<Void>()
		{
			public IFuture<Void> execute(IEvent event, IRule<Void> rule, Object context, Object condresult)
			{
				// Remove rule and continue after wait
				bdif.getRuleSystem().getRulebase().removeRule(rule.getName());
				ret.setResult(null);
				return IFuture.DONE;
			}
		});
		rule.setEvents(events);
		bdif.getRuleSystem().getRulebase().addRule(rule);
		ret.get(timeout);
	}
	
	/**
	 *  Wait for ever (is aborted on goal success/failure).
	 */
	public void waitForEver()
	{
		checkNotInAtomic();
		
		Future<Void> ret = new Future<Void>();
		ret.get();
	}
	 
	/**
	 *  Get the plan interface.
	 */
	public IPlan getPlanElement()
	{
		return rplan;
	}
	
	/**
	 *  Check if wait is called in atomic mode.
	 *  @throws RuntimeException in case is in atomic block. 
	 */
	protected void checkNotInAtomic()
	{
		if(rplan.isAtomic())
			throw new RuntimeException("Wait not allowing in atomic block.");
	}
	
	/**
	 *  The plan wait abstraction extends wait abstraction with convenience methods.
	 */
	public class PlanWaitAbstraction 
	{
		/**
		 *  Get the plan waitqueue waitabstraction.
		 */
		protected WaitAbstraction getWaitAbstraction()
		{
			return rplan.getOrCreateWaitqueueWaitAbstraction();
		}
		
		/**
		 *  Add an internal event.
		 *  @param type The type.
		 */
		public void addInternalEvent(String event)
		{
			BDIXModel model = (BDIXModel)agent.getModel().getRawModel();
			// todo: add capability name of scope
			MInternalEvent ievent = model.getCapability().getInternalEvent(event);
			if(ievent==null)
				throw new RuntimeException("Unknown internal event: "+event);
			getWaitAbstraction().addModelElement(ievent);
		}
		
		/**
		 *  Remove an internal event.
		 *  @param type The type.
		 */
		public void removeInternalEvent(String event)
		{
			BDIXModel model = (BDIXModel)agent.getModel().getRawModel();
			// todo: add capability name of scope
			MInternalEvent ievent = model.getCapability().getInternalEvent(event);
			if(ievent==null)
				throw new RuntimeException("Unknown internal event: "+event);
			getWaitAbstraction().removeModelElement(ievent);
		}
		
		/**
		 *  Add a message event.
		 *  @param type The type.
		 */
		public void addMessageEvent(String event)
		{
			BDIXModel model = (BDIXModel)agent.getModel().getRawModel();
			// todo: add capability name of scope
			MMessageEvent mevent = model.getCapability().getMessageEvent(event);
			if(mevent==null)
				throw new RuntimeException("Unknown message event: "+event);
			getWaitAbstraction().addModelElement(mevent);
		}
		
		/**
		 *  Remove a message event.
		 *  @param type The type.
		 */
		public void removeMessageEvent(String event)
		{
			BDIXModel model = (BDIXModel)agent.getModel().getRawModel();
			// todo: add capability name of scope
			MMessageEvent mevent = model.getCapability().getMessageEvent(event);
			if(mevent==null)
				throw new RuntimeException("Unknown message event: "+event);
			getWaitAbstraction().removeModelElement(mevent);
		}
		
		/**
		 *  Add the goal to wait for.
		 */
		public void addGoal(IGoal goal)
		{
			getWaitAbstraction().addRuntimeElement((RElement)goal);
		}
		
		/**
		 *  Remove the goal to wait for.
		 */
		public void removeGoal(IGoal goal)
		{
			getWaitAbstraction().removeRuntimeElement((RElement)goal);
		}
		
		/**
		 *  Add a fact added.
		 *  @param beliefset The beliefset.
		 */
		public void addFactAdded(String beliefset)
		{
			addChangeEventType(ChangeEvent.FACTADDED+"."+beliefset);
		}
		
		/**
		 *  Add a fact removed.
		 *  @param beliefset The beliefset.
		 */
		public void addFactRemoved(String beliefset)
		{
			addChangeEventType(ChangeEvent.FACTREMOVED+"."+beliefset);
		}
		
		/**
		 *  Add a belief change type.
		 *  @param belief The belief.
		 */
		public void addBeliefChanged(String belief)
		{
			addChangeEventType(ChangeEvent.BELIEFCHANGED+"."+belief);
		}
		
		/**
		 *  Add a message event.
		 *  @param type The type.
		 */
		public void addMessageEvent(MMessageEvent mevent)
		{
			getWaitAbstraction().addModelElement(mevent);
		}
		
		/**
		 *  Add a message event reply.
		 *  @param me The message event.
		 */
		public void addReply(IMessageEvent mevent)
		{
			getWaitAbstraction().addReply((RMessageEvent)mevent);
		}

		/**
		 *  Add an internal event.
		 *  @param type The type.
		 */
		public void addInternalEvent(MInternalEvent mevent)
		{
			getWaitAbstraction().addModelElement(mevent);
		}
		
//		/**
//		 *  Add a goal.
//		 *  @param type The type.
//		 */
//		public void addGoal(MGoal mgoal)
//		{
//		}
	//
//		/**
//		 *  Add a goal.
//		 *  @param goal The goal.
//		 */
//		public void addGoal(RGoal rgoal)
//		{
//		}

//		/**
//		 *  Add a fact changed.
//		 *  @param belief The belief or beliefset.
//		 */
//		public void addFactChanged(String belief)
//		{
//		}
	//
//		/**
//		 *  Add a fact added.
//		 *  @param beliefset The beliefset.
//		 */
//		public IWaitAbstraction addFactAdded(String beliefset)
//		{
//		}


//		/**
//		 *  Add a fact removed.
//		 *  @param beliefset The beliefset.
//		 */
//		public IWaitAbstraction addFactRemoved(String beliefset)
//		{
//		}
	//	
//		/**
//		 *  Add a condition.
//		 *  @param condition the condition name.
//		 */
//		public IWaitAbstraction addCondition(String condition)
//		{
//		}
	//
//		/**
//		 *  Add an external condition.
//		 *  @param condition the condition.
//		 */
//		public IWaitAbstraction addExternalCondition(IExternalCondition condition)
//		{
//		}

		//-------- remover methods --------

		/**
		 *  Remove a message event.
		 *  @param type The type.
		 */
		public void removeMessageEvent(MMessageEvent mevent)
		{
			getWaitAbstraction().removeModelElement(mevent);
		}

		/**
		 *  Remove a message event reply.
		 *  @param me The message event.
		 */
		public void removeReply(IMessageEvent me)
		{
			getWaitAbstraction().addReply((RMessageEvent)me);
		}

//		/**
//		 *  Remove an internal event.
//		 *  @param type The type.
//		 */
//		public void removeInternalEvent(MInternalEvent mevent)
//		{
//		}
	//
//		/**
//		 *  Remove a goal.
//		 *  @param type The type.
//		 */
//		public void removeGoal(MGoal mgoal)
//		{
//		}
	//
//		/**
//		 *  Remove a goal.
//		 *  @param goal The goal.
//		 */
//		public void removeGoal(RGoal rgoal)
//		{
//		}
		
//		/**
//		 *  Remove a fact changed.
//		 *  @param belief The belief or beliefset.
//		 */
//		public void removeFactChanged(String belief);
	//
//		/**
//		 *  Remove a fact added.
//		 *  @param beliefset The beliefset.
//		 */
//		public void removeFactAdded(String beliefset);
	//
	//
//		/**
//		 *  Remove a fact removed.
//		 *  @param beliefset The beliefset.
//		 */
//		public void removeFactRemoved(String beliefset);	
	//
//		/**
//		 *  Remove a condition.
//		 *  @param condition the condition name.
//		 */
//		public void removeCondition(String condition);
	//	
//		/**
//		 *  Remove an external condition.
//		 *  @param condition the condition.
//		 */
//		public void	removeExternalCondition(IExternalCondition condition);
		
		/**
		 *  Add a runtime element.
		 *  @param relement The runtime element.
		 */
		public void addRuntimeElement(RElement relement)
		{
			getWaitAbstraction().addRuntimeElement(relement);
		}
		
		/**
		 *  Remove a runtime element.
		 *  @param relement The runtime element.
		 */
		public void removeRuntimeElement(RElement relement)
		{
			getWaitAbstraction().removeRuntimeElement(relement);
		}
		
		/**
		 *  Add a change event type.
		 *  @param eventtype The change event type.
		 */
		public void addChangeEventType(String eventtype)
		{
			getWaitAbstraction().addChangeEventType(eventtype);
			rplan.setupEventsRule(getWaitAbstraction().getChangeeventtypes());
		}
		
		/**
		 *  Remove a change event type.
		 *  @param eventtype The change event type.
		 */
		public void removeChangeEventType(String eventtype)
		{
			getWaitAbstraction().removeChangeEventType(eventtype);
			rplan.setupEventsRule(getWaitAbstraction().getChangeeventtypes());
		}
		
		/**
		 *  Test if waitqueue is empty.
		 */
		public boolean isEmpty()
		{
			return rplan.getWaitqueue().isEmpty();
		}
		
		/**
		 *  Get the currently contained elements of the waitqueue.
		 *  @return The collected elements.
		 */
		public Object[] getElements()
		{
			return rplan.getWaitqueue().toArray();
		}
	}
}
