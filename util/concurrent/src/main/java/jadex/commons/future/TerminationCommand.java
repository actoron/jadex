package jadex.commons.future;

/**
 *  Base implementation of termination command to be used for sub-classing.
 */
public abstract class TerminationCommand implements ITerminationCommand
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
	public boolean	checkTermination(Exception reason)
	{
		return true;
	}
	
	/**
	 *  Called after termination was performed.
	 *  Guaranteed to be called only once for each future
	 *  and only if the termination finished the future
	 *  (i.e. is not called when the future was already finished).
	 *  @param reason The reason supplied for termination.
	 */
	public abstract void	terminated(Exception reason);
}
