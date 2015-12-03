package jadex.bpmn.runtime.exttask;

import java.util.Map;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IExternalTask
{
	/**
	 *  Execute the task.
	 *  @return	To be notified, when the task has completed.
	 */
	public IFuture<Map<String, Object>> execute();
	
	/**
	 *  Cleanup in case the task is cancelled.
	 *  @return	A future to indicate when cancellation has completed.
	 */
	public IFuture<Void> cancel();
}
