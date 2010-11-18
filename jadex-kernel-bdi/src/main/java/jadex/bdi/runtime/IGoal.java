package jadex.bdi.runtime;


/**
 *  The supertype for all goals (concrete and referenced)
 *  and all goal types (perform, achieve, get, maintain).
 */
public interface IGoal	extends IProcessableElement
{
	//-------- BDI flags --------

	/**
	 *  Get the retry flag.
	 */
	public boolean	isRetry();

	/**
	 *  Get the retry delay expression (if any).
	 */
	public long	getRetryDelay();

	/**
	 *  Get the exclude mode.
	 *  @return The exclude mode.
	 */
	public String	getExcludeMode();

	/**
	 *  Get the recur flag.
	 */
	public boolean	isRecur();

	/**
	 *  Get the recur delay expression (if any).
	 */
	public long	getRecurDelay();

	//-------- methods --------

	/**
	 *  Get the activation state.
	 *  @return True, if the goal is active.
	 */
	public boolean	isActive();

	/**
	 *  Check if goal is adopted
	 *  @return True, if the goal is adopted.
	 */
	public boolean	isAdopted();

	/**
	 *  Get the lifecycle state.
	 *  @return The current lifecycle state (e.g. new, active, dropped).
	 */
	public String	getLifecycleState();

	/**
	 *  Test if a goal is finished.
	 *  @return True, if goal is finished.
	 */
	public boolean isFinished();

	/**
	 *  Test if a goal is succeeded.
	 *  This has different meanings for the different goal types.
	 *  @return True, if goal is succeeded.
	 */
	public boolean isSucceeded();

	/**
	 *  Test if a goal is failed.
	 *  This has different meanings for the different goal types.
	 *  @return True, if goal has failed.
	 */
//	public boolean isFailed();

	/**
	 *  Drop this goal.
	 *  Causes all associated process goals
	 *  and subgoals to be dropped.
	 */
	public void drop();

	/**
	 *  Get the exception (if any).
	 *  When the goal has failed, the exception can be inspected.
	 *  If more than one plan has been executed for a goal
	 *  only the last exception will be available.
	 */
	public Exception getException();
	
	//-------- parameter handling --------

	/**
	 *  Set the result for the goal.
	 *  This is a convenience method, as the goal result
	 *  is stored as property.
	 *  @param result The result.
	 *  @ deprecated
	 */
//	public void	setResult(Object result);

	/**
	 *  Get the result of the goal.
	 *  This is a convenience method, as the goal result
	 *  is stored as property.
	 *  @return The result value.
	 *  @ deprecated
	 */
//	public Object	getResult();

	/**
	 *  Get the filter to wait for an info event.
	 *  @return The filter.
	 */
//	public IFilter getFilter();
	
	//-------- listeners --------
	
	/**
	 *  Add a goal listener.
	 *  @param listener The goal listener.
	 */
	public void addGoalListener(IGoalListener listener);
	
	/**
	 *  Remove a goal listener.
	 *  @param listener The goal listener.
	 */
	public void removeGoalListener(IGoalListener listener);
}
