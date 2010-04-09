package jadex.bdi.runtime.impl;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.GoalFailureException;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.TimeoutException;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.interpreter.PlanRules;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.InterpreterTimedObject;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;
import jadex.service.clock.IClockService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  External access interface.
 */
public class ExternalAccessFlyweight extends CapabilityFlyweight implements IBDIExternalAccess
{
	//-------- constructors --------
	
	/**
	 *  Create a new capability flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param adapter	The adapter.
	 */
	public ExternalAccessFlyweight(IOAVState state, Object scope)
	{
		super(state, scope);
	}

	//-------- goalbase shortcut methods --------

	/**
	 *  Dispatch a new top-level goal.
	 *  @param goal The new goal.
	 *  Note: plan step is interrupted after call.
	 */
	public void dispatchTopLevelGoal(IGoal goal)
	{
		getGoalbase().dispatchTopLevelGoal(goal);
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

	//-------- eventbase shortcut methods --------

	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public void	sendMessage(IMessageEvent me)
	{
		getEventbase().sendMessage(me);
	}

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

	//-------- methods --------

	/**
	 *  Wait for a some time.
	 *  @param duration The duration.
	 */
	public void	waitFor(long duration)
	{
		if(!getInterpreter().isPlanThread())
		{
			waitForExternalAccessWaitAbstraction(null, duration);
		}
		else
		{
			PlanRules.waitForWaitAbstraction(null, duration, getState(), getScope(), getInterpreter().getCurrentPlan());
		}
	}
	
	/**
	 *  Wait for a tick.
	 */
	public void	waitForTick()
	{
		if(!getInterpreter().isPlanThread())
		{
			waitForExternalAccessWaitAbstraction(null, PlanRules.TICK_TIMER);
		}
		else
		{
			PlanRules.waitForWaitAbstraction(null, PlanRules.TICK_TIMER, getState(), getScope(), getInterpreter().getCurrentPlan());
		}
	}

	/**
	 *  Wait for a condition to be satisfied.
	 *  @param condition The condition.
	 * /
	public void	waitForCondition(ICondition condition)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Wait for a condition or until the timeout occurs.
	 *  @param condition The condition.
	 *  @param timeout The timeout.
	 * /
	public void waitForCondition(ICondition condition, long timeout)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Wait for a condition to be satisfied.
	 *  @param condition The condition.
	 * /
	public void	waitForCondition(String condition)
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Wait for a condition to be satisfied.
	 *  @param condition The condition.
	 * /
	public void	waitForCondition(String condition, long timeout)
	{
		throw new UnsupportedOperationException();
	}*/

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
	public IInternalEvent waitForInternalEvent(final String type, final long timeout)
	{
		if(!getInterpreter().isPlanThread())
		{
			final AgentInvocation	invoc	= new AgentInvocation()
			{
				public void run()
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					WaitAbstractionFlyweight.addInternalEvent(wa, type, getState(), getScope());
					object	= wa;
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
				}
			};
			return (IInternalEvent)waitForExternalAccessWaitAbstraction(invoc.object, timeout);
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			WaitAbstractionFlyweight.addInternalEvent(wa, type, getState(), getScope());
			return (IInternalEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
		}
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
	public IMessageEvent sendMessageAndWait(final IMessageEvent me, final long timeout)
	{
		if(!getInterpreter().isPlanThread())
		{
			synchronized(Thread.currentThread())
			{
				AgentInvocation	invoc	= new AgentInvocation()
				{
					public void run()
					{
						Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
						WaitAbstractionFlyweight.addReply(wa, me, getState(), getScope());
						object	= wa;
						getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
					}
				};
				Object[] tmp = initializeWait(invoc.object, timeout);
				sendMessage(me);
				doWait();
				return (IMessageEvent)afterWait(invoc.object, tmp[0], (WakeupAction)tmp[1]);
			}
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			WaitAbstractionFlyweight.addReply(wa, me, getState(), getScope());
			Object[] ret = PlanRules.initializeWait(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
			sendMessage(me);
			PlanRules.doWait(getState(), getInterpreter().getCurrentPlan());
			return (IMessageEvent)PlanRules.afterWait(wa, (boolean[])ret[1], getState(), getScope(), getInterpreter().getCurrentPlan());
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
	public IMessageEvent waitForMessageEvent(final String type, final long timeout)
	{
		if(!getInterpreter().isPlanThread())
		{
			AgentInvocation	invoc	= new AgentInvocation()
			{
				public void run()
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					WaitAbstractionFlyweight.addMessageEvent(wa, type, getState(), getScope());
					object	= wa;
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
				}
			};
			return (IMessageEvent)waitForExternalAccessWaitAbstraction(invoc.object, timeout);
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			WaitAbstractionFlyweight.addMessageEvent(wa, type, getState(), getScope());
			return (IMessageEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
		}
	}

	/**
	 *  Wait for a message.
	 *  @param msgevent The message event.
	 */
	public IMessageEvent waitForReply(IMessageEvent msgevent)
	{
		return waitForReply(msgevent);
	}

	/**
	 *  Wait for a message.
	 *  @param msgevent The message event.
	 */
	public IMessageEvent waitForReply(final IMessageEvent msgevent, final long timeout)
	{
		if(!getInterpreter().isPlanThread())
		{
			AgentInvocation	invoc	= new AgentInvocation()
			{
				public void run()
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					WaitAbstractionFlyweight.addReply(wa, msgevent, getState(), getScope());
					object	= wa;
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
				}
			};
			return (IMessageEvent)waitForExternalAccessWaitAbstraction(invoc.object, timeout);
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			WaitAbstractionFlyweight.addReply(wa, msgevent, getState(), getScope());
			return (IMessageEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
		}
	}

	/**
	 *  Wait for a goal.
	 *  @param type The goal type.
	 */
	public void waitForGoal(String type)
	{
		waitForGoal(type, -1);
	}

	/**
	 *  Wait for a goal.
	 *  @param type The goal type.
	 *  @param timeout The timeout.
	 */
	public void waitForGoal(final String type, final long timeout)
	{
		if(!getInterpreter().isPlanThread())
		{
			AgentInvocation	invoc	= new AgentInvocation()
			{
				public void run()
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					WaitAbstractionFlyweight.addGoal(wa, type, getState(), getScope());
					object	= wa;
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
				}
			};
			waitForExternalAccessWaitAbstraction(invoc.object, timeout);
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			WaitAbstractionFlyweight.addGoal(wa, type, getState(), getScope());
			PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
		}
	}

	/**
	 *  Wait for a belief (set) fact change.
	 *  @param belief The belief (set) type.
	 *  @return The changed fact value.
	 */
	public Object waitForFactChanged(String belief)
	{
		return waitForFactChanged(belief, -1);
	}

	/**
	 *  Wait for a belief (set) fact change.
	 *  @param belief The belief (set) type.
	 *  @param timeout The timeout.
	 *  @return The changed fact.
	 */
	public Object waitForFactChanged(final String belief, final long timeout)
	{
		if(!getInterpreter().isPlanThread())
		{
			AgentInvocation	invoc	= new AgentInvocation()
			{
				public void run()
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					WaitAbstractionFlyweight.addFactChanged(wa, belief, getState(), getScope());
					object	= wa;
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
				}
			};
			return waitForExternalAccessWaitAbstraction(invoc.object, timeout);
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			WaitAbstractionFlyweight.addFactChanged(wa, belief, getState(), getScope());
			return PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
		}
	}

	/**
	 *  Wait for a belief set change.
	 *  @param type The belief set type.
	 *  @return The fact that was added.
	 */
	public Object waitForFactAdded(String type)
	{
		return waitForFactAdded(type, -1);
	}

	/**
	 *  Wait for a belief set change.
	 *  @param type The belief set type.
	 *  @param timeout The timeout.
	 *  @return The fact that was added.
	 */
	public Object waitForFactAdded(final String type, final long timeout)
	{
		if(!getInterpreter().isPlanThread())
		{
			AgentInvocation	invoc	= new AgentInvocation()
			{
				public void run()
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					WaitAbstractionFlyweight.addFactAdded(wa, type, getState(), getScope());
					object	= wa;
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
				}
			};
			return waitForExternalAccessWaitAbstraction(invoc.object, timeout);
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			WaitAbstractionFlyweight.addFactAdded(wa, type, getState(), getScope());
			return PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
		}
	}

	/**
	 *  Wait for a belief set change.
	 *  @param type The belief set type.
	 *  @return The fact that was added.
	 */
	public Object waitForFactRemoved(String type)
	{
		return waitForFactRemoved(type, -1);
	}

	/**
	 *  Wait for a belief set change.
	 *  @param type The belief set type.
	 *  @param timeout The timeout.
	 *  @return The fact that was added.
	 */
	public Object waitForFactRemoved(final String type, final long timeout)
	{
		if(!getInterpreter().isPlanThread())
		{
			AgentInvocation	invoc	= new AgentInvocation()
			{
				public void run()
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					WaitAbstractionFlyweight.addFactRemoved(wa, type, getState(), getScope());
					object	= wa;
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
				}
			};
			return waitForExternalAccessWaitAbstraction(invoc.object, timeout);
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			WaitAbstractionFlyweight.addFactRemoved(wa, type, getState(), getScope());
			return PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
		}
	}

	/**
	 *  Dispatch a top level goal and wait for the result.
	 *  @param goal The goal.
	 */
	public void dispatchTopLevelGoalAndWait(IGoal goal)
	{
		dispatchTopLevelGoalAndWait(goal, -1);
	}

	/**
	 *  Dispatch a top level goal and wait for the result.
	 *  @param goal The goal.
	 */
	public void dispatchTopLevelGoalAndWait(final IGoal goal, final long timeout)
	{
		if(!getInterpreter().isPlanThread())
		{
			synchronized(Thread.currentThread())
			{
				AgentInvocation	invoc	= new AgentInvocation()
				{
					public void run()
					{
						Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
						WaitAbstractionFlyweight.addGoal(wa, goal, getState(), getScope());
						object	= wa;
						getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
					}
				};
				Object[] tmp = initializeWait(invoc.object, timeout);
				dispatchTopLevelGoal(goal);
				doWait();
				afterWait(invoc.object, tmp[0], (WakeupAction)tmp[1]);
			}
		}
		else
		{
			dispatchTopLevelGoal(goal);
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			WaitAbstractionFlyweight.addGoal(wa, goal, getState(), getScope());
			PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
		}
	}
	
	/**
	 *  Wait for the agent to terminate.
	 * /
	public void waitForAgentTerminating()
	{
		throw new UnsupportedOperationException();
	}*/

	/**
	 *  Wait for the agent to terminate.
	 * /
	public void waitForAgentTerminating(long timeout)
	{
		throw new UnsupportedOperationException();
	}*/
	
	//-------- handling (synchronized with agent) external threads --------
	
	/**
	 *  Start an external thread to the set of threads which
	 *  are synchronized with the agent execution.
	 * /
	public void startSynchronizedExternalThread(Runnable runnable)
	{
		throw new UnsupportedOperationException();
	}*/
	
	/**
	 *  Add an external thread to the set of threads which
	 *  are synchronized with the agent execution.
	 * /
	public void addSynchronizedExternalThread(Thread external)
	{
		throw new UnsupportedOperationException();
	}*/
	
	/**
	 *  Remove an external thread from the set of threads that
	 *  get synchronized with agent thread.
	 * /
	public void removeSynchronizedExternalThread(Thread external)
	{
		throw new UnsupportedOperationException();
	}*/
		
	/**
	 *  Wait for a wait abstraction.
	 *  @param waitabstraction.
	 *  @return The dispatched element.
	 * /
	public Object waitForPlanWaitAbstraction(IWaitAbstraction waitabs, long timeout)
	{
		Object ret = null;
		
		final Object rplan = getInterpreter().getCurrentPlan();
		final Object rcapa = getScope(); // todo: where to get right scopes?! from?
		
		if(waitabs!=null)
		{
			Object wa = ((ElementFlyweight)waitabs).getHandle();
			if(wa!=null)
			{
				Collection rgoals = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goals);
				if(rgoals!=null)
				{
					for(Iterator it=rgoals.iterator(); it.hasNext(); )
					{
						Object rgoal = it.next();
						if(IGoal.LIFECYCLESTATE_DROPPED.equals(getState().getAttributeValue(rgoal, OAVBDIRuntimeModel.goal_has_lifecyclestate)))
							ret = new GoalFlyweight(getState(), rcapa, rgoal);	
					}
				}
			}
			
			getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitabstraction, wa);
		}
		
		if(ret==null)
		{
			final boolean[] to = new boolean[1];
			if(timeout>-1)
			{
//				final long start = System.currentTimeMillis(); 
				ITimer timer = getInterpreter().getAgentAdapter().getClock().createTimer(timeout, new ITimedObject()
				{
					public void timeEventOccurred()
					{
						getInterpreter().addExternal(new Runnable()
						{
							public void run()
							{
//								System.out.println("Timer occurred: "+start);
								// todo: test if already canceled?!
								if(getState().containsObject(rplan))
								{
									to[0] = true;
									getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_processingstate, 
										OAVBDIRuntimeModel.PLANPROCESSINGTATE_READY);
									getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitabstraction, null);
									getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement, null);
									getInterpreter().getAgentAdapter().wakeup();
								}
							}
						});
					}
				});
//				System.out.println("Timer created: "+start);
				getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_timer, timer);
			}
			
			IPlanExecutor exe = getInterpreter().getPlanExecutor(rplan);
			exe.eventWaitFor(getInterpreter(), rplan);
			
			// Code after wait.
			
			getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitabstraction, null);
			
			if(to[0])
			{
				if(waitabs!=null)
					throw new TimeoutException();
			}
			else
			{
				// Cancel and delete timer.
				ITimer timer = (ITimer)getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_timer);
				if(timer!=null)
				{
					timer.cancel();
					getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_timer, null);
				}
			}
	
			Object de = getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_dispatchedelement);
			if(de!=null)
			{
				OAVObjectType type = getState().getType(de);
				if(OAVBDIRuntimeModel.goal_type.equals(type))
				{
					// If goal is not finished and is subgoal it is contained in plan_has_subgoals.
					Collection subgoals = getState().getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_subgoals);
					if(subgoals!=null && subgoals.contains(de))
					{
						System.out.println("Dropping unfinished subgoal: "+de);
						getState().setAttributeValue(de, OAVBDIRuntimeModel.goal_has_lifecyclestate, 
							IGoal.LIFECYCLESTATE_DROPPING);
					}
					if(!OAVBDIRuntimeModel.GOALPROCESSINGSTATE_SUCCEEDED.equals(
						getState().getAttributeValue(de, OAVBDIRuntimeModel.goal_has_processingstate)))
					{
						throw new GoalFailureException("Goal failed: "+de);
					}
					ret = new GoalFlyweight(getState(), rcapa, de);
				}
				else if(OAVBDIRuntimeModel.internalevent_type.equals(type))
				{
					ret = new InternalEventFlyweight(getState(), rcapa, de);
				}
				else if(OAVBDIRuntimeModel.messageevent_type.equals(type))
				{
					ret = new MessageEventFlyweight(getState(), rcapa, de);
				}
			}
		}
		
		return ret;
	}*/
	
	/**
	 *  Wait for a wait abstraction.
	 *  @param waitabstraction.
	 *  @return The dispatched element.
	 */
	protected Object waitForExternalAccessWaitAbstraction(Object wa, long timeout)
	{
		Object ret = null;
		
		synchronized(Thread.currentThread())
		{
			Object[] tmp = initializeWait(wa, timeout);
			doWait();
			try
			{
				ret = afterWait(wa, tmp[0], (WakeupAction)tmp[1]);
			}
			finally
			{
				if(tmp[2]!=null)
				{
					Object[]	observeds	= (Object[])tmp[2];
					for(int i=0; i<observeds.length; i++)
						getInterpreter().getEventReificator().removeObservedElement(observeds[i]);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Initialize the wait abstraction within the agent.
	 *  Must be done before actions (such as dispatchTopLevelGoal) are taken because
	 *  the agent runs asynchronously and could be finished before wait is installed. 
	 */
	protected Object[] initializeWait(final Object wa, final long timeout)
	{
		final Object rcapa = getScope();
//		final boolean[] to = new boolean[1];
		final Thread callerthread = Thread.currentThread();
		final WakeupAction wakeup = new WakeupAction(callerthread);
		
		AgentInvocation invoc = new AgentInvocation()
		{
			public void run()
			{
				Object ea = getState().createObject(OAVBDIRuntimeModel.externalaccess_type);
				getState().addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_externalaccesses, ea);
				
				// Prohibid wakeup execution in a) agent and b) timer until this thread sleeps. 
			
				getState().setAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_wakeupaction, wakeup);
			
				getState().setAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_waitabstraction, wa);
				
				if(timeout>-1)
				{
//					final long start = System.currentTimeMillis(); 
					
//					System.out.println("Timer created: "+start);
					getState().setAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_timer, ((IClockService)getInterpreter()
						.getAgentAdapter().getServiceContainer().getService(IClockService.class)).createTimer(timeout, new InterpreterTimedObject(BDIInterpreter.getInterpreter(getState()).getAgentAdapter(), wakeup)));
				}
				else if(timeout==PlanRules.TICK_TIMER)
				{
					getState().setAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_timer, ((IClockService)getInterpreter()
						.getAgentAdapter().getServiceContainer().getService(IClockService.class)).createTickTimer(new InterpreterTimedObject(BDIInterpreter.getInterpreter(getState()).getAgentAdapter(), wakeup)));
				}
			
				object = ea;

				if(wa!=null)
				{
					List	observedobjects	= null;
					Collection	coll	= getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds);
					if(coll!=null)
					{
						if(observedobjects==null)
							observedobjects	= new ArrayList();
						observedobjects.addAll(coll);
					}
					coll	= getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds);
					if(coll!=null)
					{
						if(observedobjects==null)
							observedobjects	= new ArrayList();
						observedobjects.addAll(coll);
					}
					coll	= getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factchangeds);
					if(coll!=null)
					{
						if(observedobjects==null)
							observedobjects	= new ArrayList();
						observedobjects.addAll(coll);
					}
					
					if(observedobjects!=null)
					{
						oarray	= observedobjects.toArray();
					}
				}
			}
		};
		
		return new Object[]{invoc.object, wakeup, invoc.oarray};
	}
	
	/**
	 *  Do the waiting, i.e. set the external caller thread to sleep mode.
	 */
	protected void doWait()
	{
		try
		{
//			System.out.println("Waiting: "+Thread.currentThread());
			Thread.currentThread().wait();
		}
		catch(InterruptedException ex)
		{
		}
//		System.out.println("Resumed: "+Thread.currentThread());
	}
	
	/**
	 *  Perform the cleanup operations after an external wait.
	 *  Mainly removes the external access object from capability and generates the result.
	 */
	protected Object afterWait(final Object wa, final Object ea, final WakeupAction wakeup)//, final boolean[] to)
	{
		AgentInvocation invoc = new AgentInvocation()
		{
			public void run()
			{
				if(wakeup.isTimeout())
//				if(to[0])
				{
					if(wa!=null)
					{
						getState().removeAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_externalaccesses, ea);
						throw new TimeoutException();
					}
				}
		
				Object de = getState().getAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_dispatchedelement);
				if(de!=null)
				{
					OAVObjectType type = getState().getType(de);
					if(OAVBDIRuntimeModel.goal_type.equals(type))
					{
						// When goal is not succeeded (or idle for maintaingoals) throw exception.
						if(!OAVBDIRuntimeModel.GOALPROCESSINGSTATE_SUCCEEDED.equals(
							getState().getAttributeValue(de, OAVBDIRuntimeModel.goal_has_processingstate)))
						{
							Object	mgoal	= getState().getAttributeValue(de, OAVBDIRuntimeModel.element_has_model);
							if(!getState().getType(mgoal).isSubtype(OAVBDIMetaModel.maintaingoal_type)
								|| !OAVBDIRuntimeModel.GOALPROCESSINGSTATE_IDLE.equals(
									getState().getAttributeValue(de, OAVBDIRuntimeModel.goal_has_processingstate)))
							{
								getState().removeAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_externalaccesses, ea);
								throw new GoalFailureException("Goal failed: "+de);
							}
						}
						object = GoalFlyweight.getGoalFlyweight(getState(), getScope(), de);
					}
					else if(OAVBDIRuntimeModel.internalevent_type.equals(type))
					{
						object = InternalEventFlyweight.getInternalEventFlyweight(getState(), getScope(), de);
					}
					else if(OAVBDIRuntimeModel.messageevent_type.equals(type))
					{
						object = MessageEventFlyweight.getMessageEventFlyweight(getState(), getScope(), de);
					}
				}
				getState().removeAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_externalaccesses, ea);
				if(wa!=null)
					getState().removeExternalObjectUsage(wa, ExternalAccessFlyweight.this);
			}
		};
		
		return invoc.object;
	}
	
	
	/**
	 *  Create a new wait abstraction.
	 *  @return The wait abstraction.
	 * /
	public IWaitAbstraction createWaitAbstraction()
	{
		return new WaitAbstractionFlyweight(getState(), getScope(), null);
	}*/
	
	/**
	 *  Get the waitqueue.
	 *  @return The waitqueue.
	 * /
	public IWaitqueue getWaitqueue()
	{
		// Right scope?
		return new WaitqueueFlyweight(getState(), getScope(), getInterpreter().getCurrentPlan());
	}*/

	/**
	 *  Invoke some code on the agent thread.
	 *  This method queues the runnable in the agent
	 *  and immediately return (i.e. probably before
	 *  the runnable has been executed).
	 */
	public void invokeLater(Runnable runnable)
	{
		getInterpreter().getAgentAdapter().invokeLater(runnable);
	}

	/**
	 *  Get the model of the component.
	 */
	public ILoadableComponentModel	getModel()
	{
		return getInterpreter().getModel();
	}
	
	/**
	 *  Get the parent (if any).
	 *  @return The parent.
	 */
	public IExternalAccess getParent()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = getInterpreter().getParent();
				}
			};
			return (IExternalAccess)invoc.object;
		}
		else
		{
			return getInterpreter().getParent();
		}
	}
}
