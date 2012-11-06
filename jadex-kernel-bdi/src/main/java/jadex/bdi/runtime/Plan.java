package jadex.bdi.runtime;

import jadex.bdi.runtime.impl.AbstractPlan;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.impl.flyweights.WaitAbstractionFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.MessageEventRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.interpreter.PlanRules;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISuspendable;

import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.beans.PropertyChangeSupport;

import java.util.Collection;

/**
 *  A plan (in our context more a plan body) contains
 *  actions e.g. for accomplishing a target state.
 */
public abstract class Plan extends AbstractPlan implements ISuspendable//, IExternalCondition
{
	//-------- methods --------

	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
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
	 */
	public void	waitForTick()
	{
		PlanRules.waitForWaitAbstraction(null, PlanRules.TICK_TIMER, getState(), getRCapability(), getRPlan());
	}

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
		SFlyweightFunctionality.addCondition(wa, condition, getState(), getRCapability());
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
		SFlyweightFunctionality.addGoal(wa, (ElementFlyweight)subgoal, getState(), getRCapability());
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
		SFlyweightFunctionality.addInternalEvent(wa, type, getState(), getRCapability());
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
		return sendMessageAndWait(me, timeout, null);
	}
	
	/**
	 *  Send a message and wait for the answer.
	 *  Adds a reply_with entry if not present, for tracking the conversation.
	 *  @param me The message event.
	 *  @param timeout The timeout.
	 *  @return The result event.
	 */
	public IMessageEvent sendMessageAndWait(IMessageEvent me, long timeout, byte[] codecids)
	{
		// Todo: check thread access.
		Object	wa	= getState().createObject(OAVBDIRuntimeModel.waitabstraction_type);
		SFlyweightFunctionality.addReply(wa, (ElementFlyweight)me, getState(), getRCapability());
		Object[] ret = PlanRules.initializeWait(wa, timeout, getState(), getRCapability(), getRPlan());
		sendMessage(me, codecids);
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
		SFlyweightFunctionality.addMessageEvent(wa, type, getState(), getRCapability());
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
		SFlyweightFunctionality.addReply(wa, (ElementFlyweight)msgevent, getState(), getRCapability());
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
		SFlyweightFunctionality.addGoal(wa, type, getState(), getRCapability());
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
			SFlyweightFunctionality.addGoal(wa, (ElementFlyweight)goal, getState(), getRCapability());
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
		SFlyweightFunctionality.addFactChanged(wa, belief, getState(), getRCapability());
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
		SFlyweightFunctionality.addFactAdded(wa, beliefset, getState(), getRCapability());
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
		SFlyweightFunctionality.addFactRemoved(wa, beliefset, getState(), getRCapability());
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
		SFlyweightFunctionality.addFactAdded(wa, beliefset, getState(), getRCapability());
		SFlyweightFunctionality.addFactRemoved(wa, beliefset, getState(), getRCapability());
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
		SFlyweightFunctionality.addExternalCondition(wa, condition, getState(), getRCapability());
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
	 *  @return The dispatched element.
	 */
	public Object waitForWaitAbstraction(IWaitAbstraction waitabs, long timeout)
	{
		return PlanRules.waitForWaitAbstraction(((ElementFlyweight)waitabs).getHandle(), timeout, getState(), getRCapability(), getRPlan());
	}
		
	//-------- ISuspendable --------
	
	/** Property change listener handling support. */
//    private PropertyChangeSupport pcs	= new PropertyChangeSupport(this);
    protected SyncResultListener lis;
    protected IFuture future;
    
	/**
	 *  Suspend the execution of the plan.
	 *  @param timeout The timeout.
	 */
	public void suspend(IFuture future, long timeout)
	{
		if(lis==null)
		{
			lis = new SyncResultListener();
		}

		if(!getInterpreter().isPlanThread())
			throw new RuntimeException("SyncResultListener may only be used from plan thread.");
		
		if(this.future!=null)
			throw new RuntimeException("Already suspended");
		
		this.future	= future;
		
//	   	if(toString().indexOf("ExtinguishFirePlan")!=-1)
//	   		System.out.println("suspend "+this+", "+lis+", "+Thread.currentThread());
	   	try
	   	{
	   		waitForExternalCondition(lis, timeout);	// Don't use waitForResult(), because of exception being thrown.
	   	}
//	   	catch(Throwable t)
//	   	{
//		   	if(toString().indexOf("ExtinguishFirePlan")!=-1)
//		   	{
//		   		System.out.println("resumed with error "+this+", "+lis);
//		   		t.printStackTrace();
//		   	}
//	   		if(t instanceof Error)
//	   			throw (Error)t;
//	   		else
//	   			throw (RuntimeException)t;
//	   	}
	   	finally
	   	{
			this.future	= null;
			lis.reset();
//		   	if(toString().indexOf("ExtinguishFirePlan")!=-1)
//		   		System.out.println("resumed "+this+", "+lis);	   		
	   	}
	}
	
	/**
	 *  Resume the execution of the plan.
	 */
	public void resume(IFuture future)
	{
		// Only wake up if still waiting for same futur
		// (invalid resume might be called from outdated future after timeout already occurred or body aborted).
		if(this.future==future)
		{
//		   	if(toString().indexOf("ExtinguishFirePlan")!=-1)
//				System.out.println("resume "+this+", "+lis);
			lis.resultAvailable(null);
		}
	}
	
	/**
	 *  Get the monitor for waiting.
	 *  @return The monitor.
	 */
	public Object getMonitor()
	{
		BDIInterpreter pi = BDIInterpreter.getInterpreter(getState());
		IPlanExecutor exe = pi==null? null: pi.getPlanExecutor(getRPlan());
		return exe==null? null: exe.getMonitor(getRPlan());
	}
    
//	/**
//	 *  Test if the condition holds.
//	 */
//	public boolean	isTrue()
//	{
//		return true;
//	}

//	/**
//	 *  Add a property change listener.
//	 */
//	public void addPropertyChangeListener(PropertyChangeListener listener)
//	{
//        pcs.addPropertyChangeListener(listener);
//    }
//
//	/**
//	 *  Remove a property change listener.
//	 */
//    public void removePropertyChangeListener(PropertyChangeListener listener)
//    {
//        pcs.removePropertyChangeListener(listener);
//    }
	
	
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
		 * @param result The result.
		 */
		public void resultAvailable(Object result)
		{
//		   	if(Plan.this.toString().indexOf("ExtinguishFirePlan")!=-1)
//		   		System.out.println("resultAvailable: "+Plan.this+", "+this+", "+result);
			SyncResultListener.this.result = result;
			SyncResultListener.this.alreadyresumed = true;
			pcs.firePropertyChange("true", Boolean.FALSE, Boolean.TRUE);
		}

		/**
		 *  Called when an exception occurred.
		 * @param exception The exception.
		 */
		public void exceptionOccurred(Exception exception)
		{
//		   	if(Plan.this.toString().indexOf("ExtinguishFirePlan")!=-1)
//		   		System.out.println("exeception: "+Plan.this+", "+this+", "+exception);
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
			return waitForResult(-1);
		}
		
		/**
		 *  Wait for the result.
		 *  @param timeout	The timeout.
		 *  @return The result.
		 *  @throws	TimeoutException when result is not available in specified time frame.
		 */
		public Object waitForResult(long timeout)
		{
			if(!getInterpreter().isPlanThread())
				throw new RuntimeException("SyncResultListener may only be used from plan thread.");
			
			if(!alreadyresumed)
			{
				this.alreadysuspended	= true;
				waitForExternalCondition(this, timeout);
			}
			
			Exception	ex	= exception;
			Object	res	= result;
			reset();
			
//			if(ex instanceof RuntimeException)
//				throw (RuntimeException)ex;
			if(ex!=null)
				throw new RuntimeException(ex);

			return res;
		}

		/**
		 *  Reset to allow listener being reused
		 */
		protected void reset()
		{
//		   	if(Plan.this.toString().indexOf("ExtinguishFirePlan")!=-1)
//		   		System.out.println("resetting: "+Plan.this+", "+this+", "+exception);
			alreadysuspended	= false;
			alreadyresumed	= false;
			exception	= null;
			result	= null;
			pcs.firePropertyChange("true", Boolean.TRUE, Boolean.FALSE);
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
