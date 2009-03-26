package jadex.bdi.runtime;

import jadex.bdi.interpreter.MessageEventRules;
import jadex.bdi.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.interpreter.PlanRules;
import jadex.bdi.runtime.impl.ElementFlyweight;
import jadex.bdi.runtime.impl.WaitAbstractionFlyweight;
import jadex.commons.concurrent.IResultListener;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;

/**
 *  A plan (in our context more a plan body) contains
 *  actions e.g. for accomplishing a target state.
 */
public abstract class Plan extends AbstractPlan
{
	//-------- methods --------

	/**
	 *  The body method is called on the
	 *  instatiated plan instance from the scheduler.
	 */
	public abstract void	body();

	/**
	 *  The passed method is called on plan success.
	 */
	public void	passed()
	{
	}

	/**
	 *  The failed method is called on plan failure/abort.
	 */
	public void	failed()
	{
	}

	/**
	 *  The plan was aborted (because of conditional goal
	 *  success or termination from outside).
	 */
	public void aborted()
	{
	}

	/**
	 *  Create a new wait abstraction.
	 *  @return The wait abstraction.
	 */
	public IWaitAbstraction createWaitAbstraction()
	{
		return WaitAbstractionFlyweight.getWaitAbstractionFlyweight(getState(),
			getRCapability(), getState().createObject(OAVBDIRuntimeModel.waitabstraction_type));
	}
	
	/**
	 *  Wait for a some time.
	 *  @param duration The duration.
	 */
	public void	waitFor(long duration)
	{
		// Todo: check thread access.
		PlanRules.waitForWaitAbstraction(null, duration, getState(), getRCapability(), getRPlan());
	}
	
	/**
	 *  Wait for a wait abstraction.
	 *  @param waitabstraction.
	 */
	public void waitForWaitAbstraction(IWaitAbstraction waitabs)
	{
		waitForWaitAbstraction(waitabs, -1);
	}
	
	/**
	 *  Halt the plan. The plan will remain in the agent until it is aborted.
	 */
	public void	waitForEver()
	{
		// Todo: check thread access.
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		PlanRules.waitForWaitAbstraction(wa, -1, getState(), getRCapability(), getRPlan());
	}
	
	
	/**
	 *  Wait for a clock tick.
	 *  todo: @param num The number of ticks?
	 * /
	public void	waitForTick()
	{
		getCapability().checkThreadAccess();
		WaitAbstraction wa = new WaitAbstraction(getRCapability());
		wa.setTickTimeout(1, getRPlan());
		eventWaitFor(wa);
	}*/

	/**
	 *  Wait for a condition to be satisfied.
	 *  @param condition The condition.
	 * /
	public void	waitForCondition(ICondition condition)
	{
		waitForCondition(condition, -1);
	}*/

	/**
	 *  Wait for a condition or until the timeout occurs.
	 *  @param condition The condition.
	 *  @param timeout The timeout.
	 * /
	public void waitForCondition(ICondition condition, long timeout)
	{
		getCapability().checkThreadAccess();
		WaitAbstraction wa = new WaitAbstraction(getRCapability());
		wa.addCondition(((ConditionWrapper)condition).getOriginalCondition());
		wa.setTimeout(timeout, getRPlan());
		eventWaitFor(wa);
	}*/

	/**
	 *  Wait for a condition to be satisfied.
	 *  @param condition The condition.
	 */
	public void	waitForCondition(String condition)
	{
		waitForCondition(condition, -1);
	}

	/**
	 *  Wait for a condition to be satisfied.
	 *  @param condition The condition.
	 */
	public void	waitForCondition(String condition, long timeout)
	{
		// Todo: check thread access.
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		WaitAbstractionFlyweight.addCondition(wa, condition, getState(), getRCapability());
		PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getRCapability(), getRPlan());
	}

	/**
	 *  A shortcut for dispatching a goal as subgoal of the active goal,,
	 *  and waiting for the subgoal to be finished (without timout).
	 *  @param subgoal The new subgoal.
	 *  @throws GoalFailureException	when the goal fails.
	 */
	public void dispatchSubgoalAndWait(IGoal subgoal)
	{
		dispatchSubgoalAndWait(subgoal, -1);
	}

	/**
	 *  A shortcut for dispatching a goal as subgoal of the active goal
	 *  and waiting for the subgoal to be finished.
	 *  Additionally the subgoal will be dropped when finished.
	 *  This differs from the dispatchSubgoal implementation.
	 *  @param subgoal The new subgoal.
	 *  @param timeout	The timeout.
	 *  @throws GoalFailureException	when the goal fails.
	 */
	public void dispatchSubgoalAndWait(IGoal subgoal, long timeout)
	{
		dispatchSubgoal(subgoal);
		// Todo: check thread access.
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		WaitAbstractionFlyweight.addGoal(wa, subgoal, getState(), getRCapability());
		PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getRCapability(), getRPlan());
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
		// Todo: check thread access.
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		WaitAbstractionFlyweight.addInternalEvent(wa, type, getState(), getRCapability());
		return (IInternalEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getRCapability(), getRPlan());
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
		// Todo: check thread access.
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		WaitAbstractionFlyweight.addReply(wa, me, getState(), getRCapability());
		Object[] ret = PlanRules.initializeWait(wa, timeout, getState(), getRCapability(), getRPlan());
		sendMessage(me);
		if(ret[0]==null)
		{
			PlanRules.doWait(getState(), getRPlan());
			ret[0] = PlanRules.afterWait(wa, (boolean[])ret[1], getState(), getRCapability(), getRPlan());
		}
		return (IMessageEvent)ret[0];
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
		// Todo: check thread access.
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		WaitAbstractionFlyweight.addMessageEvent(wa, type, getState(), getRCapability());
		return (IMessageEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getRCapability(), getRPlan());
	}

	/**
	 *  Wait for a message.
	 *  @param msgevent The message event.
	 */
	public IMessageEvent	waitForReply(IMessageEvent msgevent)
	{
		return waitForReply(msgevent, -1);
	}

	/**
	 *  Wait for a message.
	 *  @param msgevent The message event.
	 */
	public IMessageEvent	waitForReply(IMessageEvent msgevent, long timeout)
	{
		// Ensure that the message event is registered and waitqueue is established. Otherwise
		// a message loss could happen.
		Object rmevent = ((ElementFlyweight)msgevent).getHandle();
		if(!MessageEventRules.containsRegisteredMessageEvents(getState(), getRCapability(), rmevent))
			throw new RuntimeException("Message event not registered: "+rmevent);
		if(!isEventRegisteredInWaitqueue(rmevent))
			throw new RuntimeException("Messages to be used in waitForReply() have to be registered" +
				"in the event in the waitqueue before(!) being sent: "+rmevent);
		
		// Todo: check thread access.
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		WaitAbstractionFlyweight.addReply(wa, msgevent, getState(), getRCapability());
		return (IMessageEvent)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getRCapability(), getRPlan());
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
		// Todo: check thread access.
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		WaitAbstractionFlyweight.addGoal(wa, type, getState(), getRCapability());
		return (IGoal)PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getRCapability(), getRPlan());
	}

	/**
	 *  Wait for a goal.
	 *  @param goal The goal.
	 */
	public void waitForGoal(IGoal goal)
	{
		waitForGoal(goal, -1);
	}

	/**
	 *  Wait for a goal.
	 *  @param goal The goal.
	 *  @param timeout The timeout.
	 */
	public void waitForGoal(final IGoal goal, long timeout)
	{
		if(goal.isFinished())
		{
			// Todo: check thread access.
			if(OAVBDIRuntimeModel.GOALPROCESSINGSTATE_FAILED.equals(
					getState().getAttributeValue(((ElementFlyweight)goal).getHandle(), OAVBDIRuntimeModel.goal_has_processingstate)))
			{
				throw new GoalFailureException("Goal failed: "+goal);
			}
		}
		else
		{
			// Todo: check thread access.
			Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
			WaitAbstractionFlyweight.addGoal(wa, goal, getState(), getRCapability());
			PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getRCapability(), getRPlan());
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
	public Object waitForFactChanged(String belief, long timeout)
	{
		// Todo: check thread access.
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		WaitAbstractionFlyweight.addFactChanged(wa, belief, getState(), getRCapability());
		return PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getRCapability(), getRPlan());
	}

	/**
	 *  Wait for a belief set fact addition.
	 *  @param beliefset The belief set type.
	 *  @return The added fact value.
	 */
	public Object waitForFactAdded(String beliefset)
	{
		return waitForFactAdded(beliefset, -1);
	}

	/**
	 *  Wait for a belief (set) fact change.
	 *  @param beliefset The belief (set) type.
	 *  @param timeout The timeout.
	 *  @return The changed fact.
	 */
	public Object waitForFactAdded(String beliefset, long timeout)
	{
		// Todo: check thread access.
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		WaitAbstractionFlyweight.addFactAdded(wa, beliefset, getState(), getRCapability());
		return PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getRCapability(), getRPlan());
	}


	/**
	 *  Wait for a belief set fact removal.
	 *  @param beliefset The belief set type.
	 *  @return The added fact value.
	 */
	public Object waitForFactRemoved(String beliefset)
	{
		return waitForFactRemoved(beliefset, -1);
	}

	/**
	 *  Wait for a belief (set) fact change.
	 *  @param beliefset The belief (set) type.
	 *  @param timeout The timeout.
	 *  @return The changed fact.
	 */
	public Object waitForFactRemoved(String beliefset, long timeout)
	{
		// Todo: check thread access.
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		WaitAbstractionFlyweight.addFactRemoved(wa, beliefset, getState(), getRCapability());
		return PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getRCapability(), getRPlan());
	}
	
	/**
	 *  Wait for a belief set fact addition or removal.
	 *  @param beliefset The belief set type.
	 *  @return The added fact value.
	 */
	public Object waitForFactAddedOrRemoved(String beliefset)
	{
		return waitForFactAddedOrRemoved(beliefset, -1);
	}

	/**
	 *  Wait for a belief (set) fact addition or removal.
	 *  @param beliefset The belief (set) type.
	 *  @param timeout The timeout.
	 *  @return The changed fact.
	 */
	public Object waitForFactAddedOrRemoved(String beliefset, long timeout)
	{
		// Todo: check thread access.
		// todo: return change event to indicate the type of change
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		WaitAbstractionFlyweight.addFactAdded(wa, beliefset, getState(), getRCapability());
		WaitAbstractionFlyweight.addFactRemoved(wa, beliefset, getState(), getRCapability());
		return PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getRCapability(), getRPlan());
	}

	/**
	 *  Wait for an external condition
	 *  @param condition The external condition.
	 */
	public void	waitForExternalCondition(IExternalCondition condition)
	{
		waitForExternalCondition(condition, -1);
	}

	/**
	 *  Wait for an external condition
	 *  @param condition The external condition.
	 *  @param timeout The timeout.
	 */
	public void	waitForExternalCondition(IExternalCondition condition, long timeout)
	{
		// Todo: check thread access.
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		WaitAbstractionFlyweight.addExternalCondition(wa, condition, getState(), getRCapability());
		PlanRules.waitForWaitAbstraction(wa, timeout, getState(), getRCapability(), getRPlan());
	}

	//-------- helper methods --------

	/**
	 *  Test if a message event has been registered at the waitqueue.
	 *  @param msgevent The message event.
	 *  @return True, if is registered.
	 */
	public boolean isEventRegisteredInWaitqueue(Object rmevent)
	{
		boolean ret = false;
		Object wqwa = getState().getAttributeValue(getRPlan(), OAVBDIRuntimeModel.plan_has_waitqueuewa);
		if(wqwa!=null)
		{
			Collection coll = getState().getAttributeValues(wqwa, OAVBDIRuntimeModel.waitabstraction_has_messageevents);
			if(coll!=null)
				ret = coll.contains(rmevent);
		}
		return ret;
	}
	
	//-------- deprecated methods --------

	/**
	 *  Wait for an event.
	 *  @param filter The event filter.
	 *  //@deprecated Should be avoided but in certain cases maybe cannot
	 * /
	public IEvent	waitFor(IFilter filter)
	{
		return waitFor(filter, -1);
	}*/

	/**
	 *  Wait for an event or until the timeout occurs.
	 *  @param filter The event filter.
	 *  @param timeout The timeout.
	 *  //@deprecated Should be avoided but in certain cases maybe cannot
	 * /
	public IEvent waitFor(IFilter filter, long timeout)
	{
		WaitAbstraction wa = new WaitAbstraction(getRCapability());
		wa.addFilter(filter);
		wa.setTimeout(timeout, getRPlan());
		return eventWaitFor(wa);
	}*/
	
	/**
	 *  Wait for a wait abstraction.
	 *  @param waitabstraction.
	 *  @return The dispatched element.
	 */
	public Object waitForWaitAbstraction(IWaitAbstraction waitabs, long timeout)
	{
		return PlanRules.waitForWaitAbstraction(((ElementFlyweight)waitabs).getHandle(), timeout, getState(), getRCapability(), getRPlan());
	}
		
	//-------- sync result listener --------

	/**
	 *  Listener that uses the suspend/resume capability of threaded plans.
	 */
	public class SyncResultListener implements IResultListener, IExternalCondition
	{
		//-------- attributes --------
		
		/** The result. */
		protected Object result;

		/** The exception. */
		protected Exception exception;
		
		/** Flag if already resumed. */
		protected boolean alreadyresumed;
		
		/** Flag if already suspended. */
		protected boolean alreadysuspended;
		
		/** Property change listener handling support. */
	    private PropertyChangeSupport pcs	= new PropertyChangeSupport(this);

	    //-------- IResultListener --------
		
		/**
		 *  Called when the result is available.
		 *  @param result The result.
		 */
		public void resultAvailable(Object result)
		{
//			System.out.println("resultAvailable: "+this+", "+result);
			SyncResultListener.this.result = result;
			SyncResultListener.this.alreadyresumed = true;
			pcs.firePropertyChange("true", Boolean.FALSE, Boolean.TRUE);
		}

		/**
		 *  Called when an exception occurred.
		 *  @param exception The exception.
		 */
		public void exceptionOccurred(Exception exception)
		{
//			System.out.println("exeception: "+this+", "+exception);
//			exception.printStackTrace();
			SyncResultListener.this.exception = exception;
			SyncResultListener.this.alreadyresumed = true;
			pcs.firePropertyChange("true", Boolean.FALSE, Boolean.TRUE);
		}
		
		//-------- methods --------
		
		/**
		 *  Wait for the result.
		 *  @return The result.
		 */
		public Object waitForResult()
		{
			if(!getInterpreter().isPlanThread())
				throw new RuntimeException("SyncResultListener may only be used from plan thread.");
			
			if(!alreadyresumed)
			{
				this.alreadysuspended	= true;
				waitForExternalCondition(this);
			}
			
			// Reset to allow listener being reused
			Exception	ex	= exception;
			Object	res	= result;
			alreadysuspended	= false;
			alreadyresumed	= false;
			exception	= null;
			result	= null;
			
			if(ex instanceof RuntimeException)
				throw (RuntimeException)ex;
			else if(ex!=null)
				throw new RuntimeException(ex);

			return res;
		}

		//-------- IExternalCondition --------
		
		/**
		 *  Test if the condition holds.
		 */
		public boolean	isTrue()
		{
			return alreadyresumed;
		}

		/**
		 *  Add a property change listener.
		 */
		public void addPropertyChangeListener(PropertyChangeListener listener)
		{
	        pcs.addPropertyChangeListener(listener);
	    }

		/**
		 *  Remove a property change listener.
		 */
	    public void removePropertyChangeListener(PropertyChangeListener listener)
	    {
	        pcs.removePropertyChangeListener(listener);
	    }
	}
}
