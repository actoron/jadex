package jadex.commons.concurrent;

/**
 * 
 */
public interface IThreadPoolStrategy
{
	/**
	 * 
	 */
	public void taskAdded();
	
	/**
	 * 
	 */
	public boolean taskFinished();
	
	/**
	 * 
	 */
	public void setThreadPool(StrategyThreadPool tp);
}
