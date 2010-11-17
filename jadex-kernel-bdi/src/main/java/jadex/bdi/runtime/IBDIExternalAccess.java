package jadex.bdi.runtime;

import jadex.bridge.IExternalAccess;
//import jadex.commons.IFuture;

/**
 *  The interface for external threads.
 */
public interface IBDIExternalAccess extends IExternalAccess//, IEACapability
{
	//-------- goalbase shortcut methods --------

//	/**
//	 *  Dispatch a new top-level goal.
//	 *  @param goal The new goal.
//	 *  Note: plan step is interrupted after call.
//	 */
//	public IFuture dispatchTopLevelGoal(IEAGoal goal);
//
//	/**
//	 *  Create a goal from a template goal.
//	 *  To be processed, the goal has to be dispatched as subgoal
//	 *  or adopted as top-level goal.
//	 *  @param type	The template goal name as specified in the ADF.
//	 *  @return The created goal.
//	 */
//	public IFuture createGoal(String type);

	//-------- eventbase shortcut methods --------

//	/**
//	 *  Send a message after some delay.
//	 *  @param me	The message event.
//	 *  @return The filter to wait for an answer.
//	 */
//	public IFuture sendMessage(IEAMessageEvent me);
//
//	/**
//	 *  Dispatch an internal event.
//	 *  @param event The event.
//	 *  Note: plan step is interrupted after call.
//	 */
//	public IFuture dispatchInternalEvent(IEAInternalEvent event);
//
//	/**
//	 *  Create a new message event.
//	 *  @return The new message event.
//	 */
//	public IFuture createMessageEvent(String type);
//
//	/**
//	 *  Create a new intenal event.
//	 *  @return The new intenal event.
//	 */
//	public IFuture createInternalEvent(String type);

//	/**
//	 *  Create a new intenal event.
//	 *  @return The new intenal event.
//	 *  @deprecated Convenience method for easy conversion to new explicit internal events.
//	 *  Will be removed in later releases.
//	 * /
//	public IInternalEvent createInternalEvent(String type, Object content);*/

	//-------- methods --------

//	/**
//	 *  Wait for a some time.
//	 *  @param duration The duration.
//	 */
//	public IFuture waitFor(long duration);
//	
//	/**
//	 *  Wait for a tick.
//	 */
//	public IFuture waitForTick();

	/**
	 *  Wait for a condition to be satisfied.
	 *  @param condition The condition.
	 */
//	public void	waitForCondition(ICondition condition);

	/**
	 *  Wait for a condition or until the timeout occurs.
	 *  @param condition The condition.
	 *  @param timeout The timeout.
	 */
//	public void waitForCondition(ICondition condition, long timeout);

	/**
	 *  Wait for a condition to be satisfied.
	 *  @param condition The condition.
	 */
//	public void	waitForCondition(String condition);

	/**
	 *  Wait for a condition to be satisfied.
	 *  @param condition The condition.
	 */
//	public void	waitForCondition(String condition, long timeout);

//	/**
//	 *  Wait for an internal event.
//	 *  @param type The internal event type.
//	 */
//	public IFuture waitForInternalEvent(String type);
//
//	/**
//	 *  Wait for an internal event.
//	 *  @param type The internal event type.
//	 *  @param timeout The timeout.
//	 */
//	public IFuture waitForInternalEvent(String type, long timeout);
//
//	/**
//	 *  Send a message and wait for the answer.
//	 *  @param me The message event.
//	 *  @return The result event.
//	 */
//	public IFuture sendMessageAndWait(IEAMessageEvent me);
//
//	/**
//	 *  Send a message and wait for the answer.
//	 *  Adds a reply_with entry if not present, for tracking the conversation.
//	 *  @param me The message event.
//	 *  @param timeout The timeout.
//	 *  @return The result event.
//	 */
//	public IFuture sendMessageAndWait(IEAMessageEvent me, long timeout);
//
//	/**
//	 *  Wait for a message event.
//	 *  @param type The message event type.
//	 */
//	public IFuture waitForMessageEvent(String type);
//
//	/**
//	 *  Wait for a message event.
//	 *  @param type The message event type.
//	 *  @param timeout The timeout.
//	 */
//	public IFuture waitForMessageEvent(String type, long timeout);
//
//	/**
//	 *  Wait for a message.
//	 *  @param msgevent The message event.
//	 */
//	public IFuture waitForReply(IEAMessageEvent msgevent);
//
//	/**
//	 *  Wait for a message.
//	 *  @param msgevent The message event.
//	 */
//	public IFuture waitForReply(IEAMessageEvent msgevent, long timeout);
//
//	/**
//	 *  Wait for a goal.
//	 *  @param type The goal type.
//	 */
//	public IFuture waitForGoal(String type);
//
//	/**
//	 *  Wait for a goal.
//	 *  @param type The goal type.
//	 *  @param timeout The timeout.
//	 */
//	public IFuture waitForGoal(String type, long timeout);
//
//	/**
//	 *  Wait for a belief (set) fact change.
//	 *  @param belief The belief (set) type.
//	 *  @return The changed fact value.
//	 */
//	public IFuture waitForFactChanged(String belief);
//
//	/**
//	 *  Wait for a belief (set) fact change.
//	 *  @param belief The belief (set) type.
//	 *  @param timeout The timeout.
//	 *  @return The changed fact.
//	 */
//	public IFuture waitForFactChanged(String belief, long timeout);
//
//	/**
//	 *  Wait for a belief set change.
//	 *  @param type The belief set type.
//	 *  @return The fact that was added.
//	 */
//	public IFuture waitForFactAdded(String type);
//
//	/**
//	 *  Wait for a belief set change.
//	 *  @param type The belief set type.
//	 *  @param timeout The timeout.
//	 *  @return The fact that was added.
//	 */
//	public IFuture waitForFactAdded(String type, long timeout);
//
//	/**
//	 *  Wait for a belief set change.
//	 *  @param type The belief set type.
//	 *  @return The fact that was added.
//	 */
//	public IFuture waitForFactRemoved(String type);
//
//	/**
//	 *  Wait for a belief set change.
//	 *  @param type The belief set type.
//	 *  @param timeout The timeout.
//	 *  @return The fact that was added.
//	 */
//	public IFuture waitForFactRemoved(String type, long timeout);
//
//	/**
//	 *  Dispatch a top level goal and wait for the result.
//	 *  @param goal The goal.
//	 */
//	public IFuture dispatchTopLevelGoalAndWait(IEAGoal goal);
//
//	/**
//	 *  Dispatch a top level goal and wait for the result.
//	 *  @param goal The goal.
//	 */
//	public IFuture dispatchTopLevelGoalAndWait(IEAGoal goal, long timeout);
	
	/**
	 *  Wait for the agent to terminate.
	 */
//	public void waitForAgentTerminating();

	/**
	 *  Wait for the agent to terminate.
	 */
//	public void waitForAgentTerminating(long timeout);
	
	//-------- handling (synchronized with agent) external threads --------
	
	/**
	 *  Invoke some code on the agent thread.
	 *  This method queues the runnable in the agent
	 *  and immediately return (i.e. probably before
	 *  the runnable has been executed).
	 */
//	public void invokeLater(Runnable runnable);

	/**
	 *  Start an external thread to the set of threads which
	 *  are synchronized with the agent execution.
	 */
//	public void startSynchronizedExternalThread(Runnable runnable);
	
	/**
	 *  Add an external thread to the set of threads which
	 *  are synchronized with the agent execution.
	 */
//	public void addSynchronizedExternalThread(Thread external);
	
	/**
	 *  Remove an external thread from the set of threads that
	 *  get synchronized with agent thread.
	 */
//	public void removeSynchronizedExternalThread(Thread external);
}
