package jadex.commons.collection;

/**
 *  Interface for scheduling a command.
 */
public interface IDelayRunner
{
	/**
	 *  Wait for a delay.
	 *  @param delay The delay.
	 *  @param step The step.
	 */
	public Runnable waitForDelay(long delay, final Runnable step);
	
	/**
	 *  Cancel the timer.
	 */
	public void cancel();
}
