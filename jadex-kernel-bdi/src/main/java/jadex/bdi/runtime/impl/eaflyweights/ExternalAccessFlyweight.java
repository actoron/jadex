package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IEAGoal;
import jadex.bdi.runtime.IEAInternalEvent;
import jadex.bdi.runtime.IEAMessageEvent;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.impl.FlyweightFunctionality;
import jadex.bdi.runtime.impl.WakeupAction;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.GoalLifecycleRules;
import jadex.bdi.runtime.interpreter.InternalEventRules;
import jadex.bdi.runtime.interpreter.MessageEventRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.interpreter.PlanRules;
import jadex.bridge.ComponentResultListener;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ILoadableComponentModel;
import jadex.bridge.InterpreterTimedObject;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.rules.state.IOAVState;
import jadex.service.IServiceProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 *  External access interface.
 */
public class ExternalAccessFlyweight extends EACapabilityFlyweight implements IBDIExternalAccess
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
	public IFuture dispatchTopLevelGoal(final IEAGoal goal)
	{
		final Future ret = new Future();
		
//		getGoalbase().dispatchTopLevelGoal(goal);
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					GoalLifecycleRules.adoptGoal(getState(), ((ElementFlyweight)goal).getScope(), ((ElementFlyweight)goal).getHandle());
					ret.setResult(null);
				}
			});
		}
		else
		{
			GoalLifecycleRules.adoptGoal(getState(), ((ElementFlyweight)goal).getScope(), ((ElementFlyweight)goal).getHandle());
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Create a goal from a template goal.
	 *  To be processed, the goal has to be dispatched as subgoal
	 *  or adopted as top-level goal.
	 *  @param type	The template goal name as specified in the ADF.
	 *  @return The created goal.
	 */
//	public IGoal createGoal(String type)
	public IFuture createGoal(final String type)
	{
//		return getGoalbase().createGoal(type);
		
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					ret.setResult(FlyweightFunctionality.createGoal(getState(), getScope(), true, type));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.createGoal(getState(), getScope(), true, type));
		}
		
		return ret;
	}

	//-------- eventbase shortcut methods --------

	/**
	 *  Send a message after some delay.
	 *  @param me	The message event.
	 *  @return The filter to wait for an answer.
	 */
	public IFuture	sendMessage(final IEAMessageEvent me)
	{
		final Future ret = new Future();
		
//		getEventbase().sendMessage(me);
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					MessageEventRules.sendMessage(getState(), getScope(), ((ElementFlyweight)me).getHandle());
					ret.setResult(null);
				}
			});
		}
		else
		{
			MessageEventRules.sendMessage(getState(), getScope(), ((ElementFlyweight)me).getHandle());
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Dispatch an internal event.
	 *  @param event The event.
	 *  Note: plan step is interrupted after call.
	 */
	public IFuture dispatchInternalEvent(final IEAInternalEvent event)
	{
		final Future ret = new Future();
		
//		getEventbase().dispatchInternalEvent(event);
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					InternalEventRules.adoptInternalEvent(getState(), getScope(), ((ElementFlyweight)event).getHandle());
					ret.setResult(null);
				}
			});
		}
		else
		{
			InternalEventRules.adoptInternalEvent(getState(), getScope(), ((ElementFlyweight)event).getHandle());
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Create a new message event.
	 *  @return The new message event.
	 */
	public IFuture createMessageEvent(final String type)
	{
//		return getEventbase().createMessageEvent(type);
		
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					ret.setResult(FlyweightFunctionality.createMessageEvent(getState(), getScope(), type, true));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.createMessageEvent(getState(), getScope(), type, true));
		}
		
		return ret;
	}

	/**
	 *  Create a new intenal event.
	 *  @return The new intenal event.
	 */
	public IFuture createInternalEvent(final String type)
	{
//		return getEventbase().createInternalEvent(type);
		
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					ret.setResult(FlyweightFunctionality.createInternalEvent(getState(), getScope(), type, true));
				}
			});
		}
		else
		{
			ret.setResult(FlyweightFunctionality.createInternalEvent(getState(), getScope(), type, true));
		}
		
		return ret;
	}

	//-------- methods --------

	/**
	 *  Wait for a some time.
	 *  @param duration The duration.
	 */
	public IFuture waitFor(long duration)
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			waitForExternalAccessWaitAbstraction(null, duration, ret);
		}
		else
		{
			PlanRules.waitForWaitAbstraction(null, duration, getState(), getScope(), getInterpreter().getCurrentPlan());
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Wait for a tick.
	 */
	public IFuture waitForTick()
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			waitForExternalAccessWaitAbstraction(null, PlanRules.TICK_TIMER, ret);
		}
		else
		{
			PlanRules.waitForWaitAbstraction(null, PlanRules.TICK_TIMER, getState(), getScope(), getInterpreter().getCurrentPlan());
			ret.setResult(null);
		}
		
		return ret;
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
	public IFuture waitForInternalEvent(String type)
	{
		return waitForInternalEvent(type, -1);
	}

	/**
	 *  Wait for an internal event.
	 *  @param type The internal event type.
	 *  @param timeout The timeout.
	 */
	public IFuture waitForInternalEvent(final String type, final long timeout)
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					FlyweightFunctionality.addInternalEvent(wa, type, getState(), getScope());
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
					waitForExternalAccessWaitAbstraction(wa, timeout, ret);
				}
			});
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			FlyweightFunctionality.addInternalEvent(wa, type, getState(), getScope());
			IInternalEvent ev = (IInternalEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
			ret.setResult(ev);
		}
		
		return ret;
		
//		if(!getInterpreter().isPlanThread())
//		{
//			final AgentInvocation	invoc	= new AgentInvocation()
//			{
//				public void run()
//				{
//					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//					WaitAbstractionFlyweight.addInternalEvent(wa, type, getState(), getScope());
//					object	= wa;
//					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
//				}
//			};
//			return (IInternalEvent)waitForExternalAccessWaitAbstraction(invoc.object, timeout);
//		}
//		else
//		{
//			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//			WaitAbstractionFlyweight.addInternalEvent(wa, type, getState(), getScope());
//			return (IInternalEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
//		}
	}

	/**
	 *  Send a message and wait for the answer.
	 *  @param me The message event.
	 *  @return The result event.
	 */
	public IFuture sendMessageAndWait(IEAMessageEvent me)
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
	public IFuture sendMessageAndWait(final IEAMessageEvent me, final long timeout)
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					FlyweightFunctionality.addReply(wa, (ElementFlyweight)me, getState(), getScope());
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
					sendMessage(me);
					waitForExternalAccessWaitAbstraction(wa, timeout, ret);
				}
			});
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			FlyweightFunctionality.addReply(wa, (ElementFlyweight)me, getState(), getScope());
			Object[] wret = PlanRules.initializeWait(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
			sendMessage(me);
			PlanRules.doWait(getState(), getInterpreter().getCurrentPlan());
			IMessageEvent ev = (IMessageEvent)PlanRules.afterWait(wa, (boolean[])wret[1], getState(), getScope(), getInterpreter().getCurrentPlan());
			ret.setResult(ev);
		}
		
		return ret;
		
//		if(!getInterpreter().isPlanThread())
//		{
//			synchronized(Thread.currentThread())
//			{
//				AgentInvocation	invoc	= new AgentInvocation()
//				{
//					public void run()
//					{
//						Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//						WaitAbstractionFlyweight.addReply(wa, me, getState(), getScope());
//						object	= wa;
//						getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
//					}
//				};
//				Object[] tmp = initializeWait(invoc.object, timeout);
//				sendMessage(me);
//				doWait();
//				return (IMessageEvent)afterWait(invoc.object, tmp[0], (WakeupAction)tmp[1]);
//			}
//		}
//		else
//		{
//			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//			WaitAbstractionFlyweight.addReply(wa, me, getState(), getScope());
//			Object[] ret = PlanRules.initializeWait(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
//			sendMessage(me);
//			PlanRules.doWait(getState(), getInterpreter().getCurrentPlan());
//			return (IMessageEvent)PlanRules.afterWait(wa, (boolean[])ret[1], getState(), getScope(), getInterpreter().getCurrentPlan());
//		}
	}

	/**
	 *  Wait for a message event.
	 *  @param type The message event type.
	 */
	public IFuture waitForMessageEvent(String type)
	{
		return waitForMessageEvent(type, -1);
	}

	/**
	 *  Wait for a message event.
	 *  @param type The message event type.
	 *  @param timeout The timeout.
	 */
	public IFuture waitForMessageEvent(final String type, final long timeout)
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					FlyweightFunctionality.addMessageEvent(wa, type, getState(), getScope());
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
					waitForExternalAccessWaitAbstraction(wa, timeout, ret);
				}
			});
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			FlyweightFunctionality.addMessageEvent(wa, type, getState(), getScope());
			IMessageEvent ev = (IMessageEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
			ret.setResult(ev);
		}
		
		return ret;
		
//		if(!getInterpreter().isPlanThread())
//		{
//			AgentInvocation	invoc	= new AgentInvocation()
//			{
//				public void run()
//				{
//					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//					WaitAbstractionFlyweight.addMessageEvent(wa, type, getState(), getScope());
//					object	= wa;
//					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
//				}
//			};
//			return (IMessageEvent)waitForExternalAccessWaitAbstraction(invoc.object, timeout);
//		}
//		else
//		{
//			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//			WaitAbstractionFlyweight.addMessageEvent(wa, type, getState(), getScope());
//			return (IMessageEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
//		}
	}

	/**
	 *  Wait for a message.
	 *  @param msgevent The message event.
	 */
	public IFuture waitForReply(IEAMessageEvent msgevent)
	{
		return waitForReply(msgevent);
	}

	/**
	 *  Wait for a message.
	 *  @param msgevent The message event.
	 */
	public IFuture waitForReply(final IEAMessageEvent msgevent, final long timeout)
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					FlyweightFunctionality.addReply(wa, (ElementFlyweight)msgevent, getState(), getScope());
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
					waitForExternalAccessWaitAbstraction(wa, timeout, ret);
				}
			});
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			FlyweightFunctionality.addReply(wa, (ElementFlyweight)msgevent, getState(), getScope());
			IMessageEvent ev = (IMessageEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
			ret.setResult(ev);
		}
		
		return ret;
		
//		if(!getInterpreter().isPlanThread())
//		{
//			AgentInvocation	invoc	= new AgentInvocation()
//			{
//				public void run()
//				{
//					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//					WaitAbstractionFlyweight.addReply(wa, msgevent, getState(), getScope());
//					object	= wa;
//					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
//				}
//			};
//			return (IMessageEvent)waitForExternalAccessWaitAbstraction(invoc.object, timeout);
//		}
//		else
//		{
//			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//			WaitAbstractionFlyweight.addReply(wa, msgevent, getState(), getScope());
//			return (IMessageEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
//		}
	}

	/**
	 *  Wait for a goal.
	 *  @param type The goal type.
	 */
	public IFuture waitForGoal(String type)
	{
		return waitForGoal(type, -1);
	}

	/**
	 *  Wait for a goal.
	 *  @param type The goal type.
	 *  @param timeout The timeout.
	 */
	public IFuture waitForGoal(final String type, final long timeout)
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					FlyweightFunctionality.addGoal(wa, type, getState(), getScope());
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
					waitForExternalAccessWaitAbstraction(wa, timeout, ret);
				}
			});
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			FlyweightFunctionality.addGoal(wa, type, getState(), getScope());
			IMessageEvent ev = (IMessageEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
			ret.setResult(ev);
		}
		
		return ret;
		
//		if(!getInterpreter().isPlanThread())
//		{
//			AgentInvocation	invoc	= new AgentInvocation()
//			{
//				public void run()
//				{
//					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//					WaitAbstractionFlyweight.addGoal(wa, type, getState(), getScope());
//					object	= wa;
//					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
//				}
//			};
//			waitForExternalAccessWaitAbstraction(invoc.object, timeout);
//		}
//		else
//		{
//			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//			WaitAbstractionFlyweight.addGoal(wa, type, getState(), getScope());
//			PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
//		}
	}

	/**
	 *  Wait for a belief (set) fact change.
	 *  @param belief The belief (set) type.
	 *  @return The changed fact value.
	 */
	public IFuture waitForFactChanged(String belief)
	{
		return waitForFactChanged(belief, -1);
	}

	/**
	 *  Wait for a belief (set) fact change.
	 *  @param belief The belief (set) type.
	 *  @param timeout The timeout.
	 *  @return The changed fact.
	 */
	public IFuture waitForFactChanged(final String belief, final long timeout)
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					FlyweightFunctionality.addFactChanged(wa, belief, getState(), getScope());
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
					waitForExternalAccessWaitAbstraction(wa, timeout, ret);
				}
			});
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			FlyweightFunctionality.addFactChanged(wa, belief, getState(), getScope());
			IMessageEvent ev = (IMessageEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
			ret.setResult(ev);
		}
		
		return ret;
		
//		if(!getInterpreter().isPlanThread())
//		{
//			AgentInvocation	invoc	= new AgentInvocation()
//			{
//				public void run()
//				{
//					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//					WaitAbstractionFlyweight.addFactChanged(wa, belief, getState(), getScope());
//					object	= wa;
//					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
//				}
//			};
//			return waitForExternalAccessWaitAbstraction(invoc.object, timeout);
//		}
//		else
//		{
//			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//			WaitAbstractionFlyweight.addFactChanged(wa, belief, getState(), getScope());
//			return PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
//		}
	}

	/**
	 *  Wait for a belief set change.
	 *  @param type The belief set type.
	 *  @return The fact that was added.
	 */
	public IFuture waitForFactAdded(String type)
	{
		return waitForFactAdded(type, -1);
	}

	/**
	 *  Wait for a belief set change.
	 *  @param type The belief set type.
	 *  @param timeout The timeout.
	 *  @return The fact that was added.
	 */
	public IFuture waitForFactAdded(final String type, final long timeout)
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					FlyweightFunctionality.addFactAdded(wa, type, getState(), getScope());
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
					waitForExternalAccessWaitAbstraction(wa, timeout, ret);
				}
			});
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			FlyweightFunctionality.addFactAdded(wa, type, getState(), getScope());
			IMessageEvent ev = (IMessageEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
			ret.setResult(ev);
		}
		
		return ret;
		
//		if(!getInterpreter().isPlanThread())
//		{
//			AgentInvocation	invoc	= new AgentInvocation()
//			{
//				public void run()
//				{
//					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//					WaitAbstractionFlyweight.addFactAdded(wa, type, getState(), getScope());
//					object	= wa;
//					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
//				}
//			};
//			return waitForExternalAccessWaitAbstraction(invoc.object, timeout);
//		}
//		else
//		{
//			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//			WaitAbstractionFlyweight.addFactAdded(wa, type, getState(), getScope());
//			return PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
//		}
	}

	/**
	 *  Wait for a belief set change.
	 *  @param type The belief set type.
	 *  @return The fact that was added.
	 */
	public IFuture waitForFactRemoved(String type)
	{
		return waitForFactRemoved(type, -1);
	}

	/**
	 *  Wait for a belief set change.
	 *  @param type The belief set type.
	 *  @param timeout The timeout.
	 *  @return The fact that was added.
	 */
	public IFuture waitForFactRemoved(final String type, final long timeout)
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					FlyweightFunctionality.addFactRemoved(wa, type, getState(), getScope());
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
					waitForExternalAccessWaitAbstraction(wa, timeout, ret);
				}
			});
		}
		else
		{
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			FlyweightFunctionality.addFactRemoved(wa, type, getState(), getScope());
			IMessageEvent ev = (IMessageEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
			ret.setResult(ev);
		}
		
		return ret;
		
//		if(!getInterpreter().isPlanThread())
//		{
//			AgentInvocation	invoc	= new AgentInvocation()
//			{
//				public void run()
//				{
//					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//					WaitAbstractionFlyweight.addFactRemoved(wa, type, getState(), getScope());
//					object	= wa;
//					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
//				}
//			};
//			return waitForExternalAccessWaitAbstraction(invoc.object, timeout);
//		}
//		else
//		{
//			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//			WaitAbstractionFlyweight.addFactRemoved(wa, type, getState(), getScope());
//			return PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
//		}
	}

	/**
	 *  Dispatch a top level goal and wait for the result.
	 *  @param goal The goal.
	 */
	public IFuture dispatchTopLevelGoalAndWait(IEAGoal goal)
	{
		return dispatchTopLevelGoalAndWait(goal, -1);
	}

	/**
	 *  Dispatch a top level goal and wait for the result.
	 *  @param goal The goal.
	 */
	public IFuture dispatchTopLevelGoalAndWait(final IEAGoal goal, final long timeout)
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
					FlyweightFunctionality.addGoal(wa, (ElementFlyweight)goal, getState(), getScope());
					getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
					dispatchTopLevelGoal(goal);
					waitForExternalAccessWaitAbstraction(wa, timeout, ret);
				}
			});
		}
		else
		{
			dispatchTopLevelGoal(goal);
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			FlyweightFunctionality.addGoal(wa, (ElementFlyweight)goal, getState(), getScope());
			IMessageEvent ev = (IMessageEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
			ret.setResult(ev);
		}
		
		return ret;
		
		
//		if(!getInterpreter().isPlanThread())
//		{
//			synchronized(Thread.currentThread())
//			{
//				AgentInvocation	invoc	= new AgentInvocation()
//				{
//					public void run()
//					{
//						Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//						WaitAbstractionFlyweight.addGoal(wa, goal, getState(), getScope());
//						object	= wa;
//						getState().addExternalObjectUsage(wa, ExternalAccessFlyweight.this);
//					}
//				};
////				System.err.println("waitforbug: initialize wait");
////				Thread.dumpStack();
//				Object[] tmp = initializeWait(invoc.object, timeout);
////				System.err.println("waitforbug: dispatch");
////				Thread.dumpStack();
//				dispatchTopLevelGoal(goal);
//				doWait();
////				System.err.println("waitforbug: after wait");
////				Thread.dumpStack();
//				afterWait(invoc.object, tmp[0], (WakeupAction)tmp[1]);
//			}
//		}
//		else
//		{
//			dispatchTopLevelGoal(goal);
//			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
//			WaitAbstractionFlyweight.addGoal(wa, goal, getState(), getScope());
//			PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getScope(), getInterpreter().getCurrentPlan());
//		}
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
	 *  Wait for a wait abstraction.
	 *  @param waitabstraction.
	 *  @return The dispatched element.
	 */
	protected void waitForExternalAccessWaitAbstraction(Object wa, long timeout, Future future)
	{
		initializeWait(wa, timeout, future);
			
//			doWait();
//			try
//			{
//				ret = afterWait(wa, tmp[0], (WakeupAction)tmp[1]);
//			}
//			finally
//			{
//				if(tmp[2]!=null)
//				{
//					Object[]	observeds	= (Object[])tmp[2];
//					for(int i=0; i<observeds.length; i++)
//						getInterpreter().getEventReificator().removeObservedElement(observeds[i]);
//				}
//			}
//		}
	}
	
	/**
	 *  Initialize the wait abstraction within the agent.
	 *  Must be done before actions (such as dispatchTopLevelGoal) are taken because
	 *  the agent runs asynchronously and could be finished before wait is installed. 
	 */
	protected void initializeWait(final Object wa, final long timeout, final Future future)
	{
		final Object rcapa = getScope();
		
		getInterpreter().getAgentAdapter().invokeLater(new Runnable()
		{
			public void run()
			{
				Object ea = getState().createObject(OAVBDIRuntimeModel.externalaccess_type);
				getState().addAttributeValue(rcapa, OAVBDIRuntimeModel.capability_has_externalaccesses, ea);
				
				// Prohibid wakeup execution in a) agent and b) timer until this thread sleeps. 
			
			
				getState().setAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_waitabstraction, wa);
			
				List observedobjects = null;
				if(wa!=null)
				{
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
				}
				
				WakeupAction wakeup = new WakeupAction(getState(), getScope(), wa, ea, ExternalAccessFlyweight.this, observedobjects, future);
				getState().setAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_wakeupaction, wakeup);

				IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_serviceprovider);
				if(timeout>-1)
				{
//					final long start = System.currentTimeMillis(); 
					
//					System.out.println("Timer created: "+start);
					getState().setAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_timer,
						getInterpreter().getClockService().createTimer(timeout, new InterpreterTimedObject(sp, getInterpreter().getAgentAdapter(), wakeup)));
				}
				else if(timeout==PlanRules.TICK_TIMER)
				{
					getState().setAttributeValue(ea, OAVBDIRuntimeModel.externalaccess_has_timer,
						getInterpreter().getClockService().createTickTimer(new InterpreterTimedObject(sp, getInterpreter().getAgentAdapter(), wakeup)));
				}				
			}
		});
		
//		return new Object[]{invoc.object, wakeup, invoc.oarray};
	}
	
	/**
	 *  Do the waiting, i.e. set the external caller thread to sleep mode.
	 */
//	protected void doWait()
//	{
//		try
//		{
////			System.out.println("Waiting: "+Thread.currentThread());
//			Thread.currentThread().wait();
//		}
//		catch(InterruptedException ex)
//		{
//		}
////		System.out.println("Resumed: "+Thread.currentThread());
//	}
	
	/**
	 *  Perform the cleanup operations after an external wait.
	 *  Mainly removes the external access object from capability and generates the result.
	 * /
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
						object = EGoalFlyweight.getGoalFlyweight(getState(), getScope(), de);
					}
					else if(OAVBDIRuntimeModel.internalevent_type.equals(type))
					{
						object = EInternalEventFlyweight.getInternalEventFlyweight(getState(), getScope(), de);
					}
					else if(OAVBDIRuntimeModel.messageevent_type.equals(type))
					{
						object = EMessageEventFlyweight.getMessageEventFlyweight(getState(), getScope(), de);
					}
				}
				getState().removeAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_externalaccesses, ea);
				if(wa!=null)
					getState().removeExternalObjectUsage(wa, ExternalAccessFlyweight.this);
			}
		};
		
		return invoc.object;
	}*/
	
	
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
	 *  Get the first declared service of a given type.
	 *  @param type The type.
	 *  @return The corresponding service.
	 */
	public IFuture getService(final Class type)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_serviceprovider);
					object = sp.getService(type);
				}
			};
			return (IFuture)invoc.object;
		}
		else
		{
			IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_serviceprovider);
			return sp.getService(type);
		}
	}
	
	/**
	 *  Get the first declared service of a given type.
	 *  @param type The type.
	 *  @return The corresponding service.
	 * /
	public IFuture getService(final Class type)
	{
		final Future ret = new Future();
		
		if(!getInterpreter().isPlanThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable() 
			{
				public void run() 
				{
					IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_serviceprovider);
					ret.setResult(sp.getService(type));
				}
			});
		}
		else
		{
			IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_serviceprovider);
			ret.setResult(sp.getService(type));
		}
		
		return ret;
	}*/
	
	/**
	 *  Get a service.
	 *  @param type The class.
	 *  @return The corresponding services.
	 */
	public IFuture getServices(final Class type)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(object, OAVBDIRuntimeModel.capability_has_abstractsources);
					object = sp.getServices(type);
				}
			};
			return (IFuture)invoc.object;
		}
		else
		{
			IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_abstractsources);
			return sp.getServices(type);
		}
	}
	
	/**
	 *  Get a service.
	 *  @param name The name.
	 *  @return The corresponding service.
	 * /
	public IFuture getService(final Class type, final String name)
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_abstractsources);
					object = sp.getService(type, name);
				}
			};
			return (IFuture)invoc.object;
		}
		else
		{
			IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_abstractsources);
			return sp.getService(type, name);
		}
	}*/
	
	/**
	 *  Get the available service types.
	 *  @return The service types.
	 */
	public IFuture getServicesTypes()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_abstractsources);
					object = sp.getServicesTypes();
				}
			};
			return (IFuture)invoc.object;
		}
		else
		{
			IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_abstractsources);
			return sp.getServicesTypes();
		}
	}
	
	/**
	 *  Get the children (if any).
	 *  @return The children.
	 */
	public IFuture getChildren()
	{
		final Future ret = new Future();
		
		getService(IComponentManagementService.class).addResultListener(new ComponentResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				IComponentIdentifier[] childs = cms.getChildren(getComponentIdentifier());
				List res = new ArrayList();
				for(int i=0; i<childs.length; i++)
				{
					IExternalAccess ex = (IExternalAccess)cms.getExternalAccess(childs[i]);
					res.add(ex);
				}
				ret.setResult(res);
			}
		}, adapter));
		
		return ret;
	}
	
	// todo: remove me?
	/**
	 *  Get all services for a type.
	 *  @param type The type.
	 */
	public IFuture getServiceOfType(final Class type, final Set visited)
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_abstractsources);
					ret.setResult(sp.getServiceOfType(type, visited));
				}
			});
		}
		else
		{
			IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_abstractsources);
			ret.setResult(sp.getServiceOfType(type, visited));
		}
		
		return ret;
	}
	
	// todo: remove me?
	/**
	 *  Get all services for a type.
	 *  @param type The type.
	 */
	public IFuture getServicesOfType(final Class type, final Set visited)
	{
		final Future ret = new Future();
		
		if(adapter.isExternalThread())
		{
			adapter.invokeLater(new Runnable() 
			{
				public void run() 
				{
					IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_abstractsources);
					ret.setResult(sp.getServicesOfType(type, visited));
				}
			});
		}
		else
		{
			IServiceProvider sp = (IServiceProvider)getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.capability_has_abstractsources);
			ret.setResult(sp.getServicesOfType(type, visited));
		}
		
		return ret;
	}

	// todo: remove me?
	/**
	 *  Get the name of the provider.
	 *  @return The name of this provider.
	 */
	public String getName()
	{
		return getComponentName();
	}
}
