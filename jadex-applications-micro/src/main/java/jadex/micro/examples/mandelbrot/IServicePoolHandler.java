package jadex.micro.examples.mandelbrot;

import jadex.commons.IFuture;
import jadex.commons.service.IService;

/**
 *  Handler for creation, selecting and invoking a service.
 */
public interface IServicePoolHandler
{
	/**
	 *  Create a service.
	 *  Optional operation only needed, when services should be dynamically created as needed.
	 *  @return	The created service as a future result.
	 */
	public IFuture	createService();
	
	/**
	 *  Select a service.
	 *  Allows restricting further, which of the discovered services are invoked.
	 *  @return	true, when the service should be selected.
	 */
	public boolean	selectService(IService service);
	
	/**
	 *  Invoke a service.
	 *  @param service	The service.
	 *  @param task	The task to execute.
	 *  @return	The future indicating that the task is finished (or failed).
	 */
	public IFuture	invokeService(IService service, Object task);
}