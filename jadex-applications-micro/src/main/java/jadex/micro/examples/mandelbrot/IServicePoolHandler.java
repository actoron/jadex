package jadex.micro.examples.mandelbrot;

import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;

/**
 *  Handler for creation, selecting and invoking a service.
 */
public interface IServicePoolHandler
{
	/**
	 *  Select a service.
	 *  Allows restricting further, which of the discovered services are invoked.
	 *  @param service	The service.
	 *  @return	True, when the service should be selected.
	 */
	public boolean	selectService(IService service);
	
	/**
	 *  Create a service.
	 *  Optional operation only needed, when services should be dynamically created as needed.
	 *  @return	The created service as a future result.
	 */
	public IFuture	createService();
	
	/**
	 *  Invoke a service.
	 *  @param service	The service.
	 *  @param task	The task to execute.
	 *  @param user	User data that was provided to the ServicePoolManager.performTasks() method (if any).
	 *  @return	The future indicating that the task is finished (or failed).
	 */
	public IFuture	invokeService(IService service, Object task, Object user);
}