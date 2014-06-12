package jadex.bdiv3.runtime;

/**
 *  Interface for a bdi goal.
 */
public interface IGoal
{
	//-------- goal states --------
	
	public static enum GoalLifecycleState
	{
		NEW, 
		ADOPTED,
		OPTION,
		ACTIVE,
		SUSPENDED,
		DROPPING,
		DROPPED
	};
	
	public static enum GoalProcessingState
	{
		IDLE, 
		INPROCESS,
		PAUSED,
		SUCCEEDED,
		FAILED,
	};
	
	/**
	 *  Get the id.
	 */
	public String getId();
	
	/**
	 *  Drop the goal.
	 */
	public void drop();
	
	/**
	 *  Get the lifecycle state.
	 *  @return The current lifecycle state (e.g. new, active, dropped).
	 */
	public GoalLifecycleState getLifecycleState();
	
	/**
	 *  Get the processingState.
	 *  @return The processingState.
	 */
	public GoalProcessingState getProcessingState();
	
	/**
	 *  Test if goal is succeeded.
	 */
	public boolean isSucceeded();
	
	/**
	 *  Test if goal is failed.
	 */
	public boolean isFailed();
	
	/**
	 *  Test if goal is finished.
	 */
	public boolean isFinished();
	
//	/**
//	 *  Get the parent of the goal.
//	 */
//	public IPlan getParent();
}
