package jadex.commons;

/**
 * 
 */
public interface ISuspendable
{
	/**
	 * 
	 */
	public void suspend(long timeout);
	
	/**
	 * 
	 */
	public void resume();
}
