package jadex.bdi.runtime;

/**
 *  Common interface for all conditions
 */
public interface IEACondition extends IEAElement
{
	//-------- listeners --------
	
	/**
	 *  Add a condition listener.
	 *  @param listener The condition listener.
	 *  @param async True, if the notification should be done on a separate thread.
	 */
	public void addConditionListener(IConditionListener listener, boolean async);
	
	/**
	 *  Remove a condition listener.
	 *  @param listener The condition listener.
	 */
	public void removeConditionListener(IConditionListener listener);
}
