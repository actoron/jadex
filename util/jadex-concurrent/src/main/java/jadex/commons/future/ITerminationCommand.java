package jadex.commons.future;

/**
 *  A command to customize termination of a terminable future.
 */
public interface ITerminationCommand
{
	/**
	 *  Check if termination is allowed.
	 *  Called before termination is performed.
	 *  Note that due to race conditions, the future may already be finished when this method executes.
	 *  If false is returned, the termination request is ignored.
	 *  
	 *  @param reason The reason supplied for termination.
	 *  @return True, if termination should proceed.
	 */
	public boolean	checkTermination(Exception reason);
	
	/**
	 *  Called after termination was performed.
	 *  Guaranteed to be called only once for each future
	 *  and only if the termination finished the future
	 *  (i.e. is not called when the future was already finished).
	 *  @param reason The reason supplied for termination.
	 */
	public void	terminated(Exception reason);
}
