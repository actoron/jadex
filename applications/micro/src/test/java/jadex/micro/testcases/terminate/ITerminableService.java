package jadex.micro.testcases.terminate;

import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

/**
 *  Service interface to test terminable futures.
 */
public interface ITerminableService
{
	/**
	 *  Get a result.
	 *  @param delay The delay that is waited before the result is returned.
	 *  @return The result.
	 */
	public ITerminableFuture<String> getResult(long delay);
	
	/**
	 *  Get three results (one initial, one after half of the time has passed and one directly before finished).
	 *  @param delay The delay that is waited before the future is set to finished.
	 *  @return The results.
	 */
	public ITerminableIntermediateFuture<String> getResults(long delay);
	
	/**
	 *  Be informed when one of the other methods futures is terminated.
	 *  Returns an initial result when this future is registered.
	 *  Is finished, when the terminate action of the other future was called.
	 */
	public IIntermediateFuture<Void>	isTerminateCalled();
}
