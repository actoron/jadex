package jadex.micro.testcases.terminate;

import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

/**
 *  Service interface that returns terminable futures.
 */
public interface ITerminableService
{
	/**
	 *  Get the result.
	 *  @param delay The delay that is waited before the result is retured.
	 *  @return The result.
	 */
	public ITerminableFuture<String> getResult(long delay);
	
	/**
	 *  Get the results.
	 *  @param delay The delay that is waited between intermediate results.
	 *  @return The results.
	 */
	public ITerminableIntermediateFuture<String> getResults(long delay, int max);
}
