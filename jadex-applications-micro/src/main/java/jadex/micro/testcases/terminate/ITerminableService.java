package jadex.micro.testcases.terminate;

import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.ITerminableFuture;

/**
 * 
 */
public interface ITerminableService
{
	/**
	 *  Get the result.
	 *  @param delay The delay that is waited before the result is retured.
	 *  @return The result.
	 */
	@Timeout(300000000)
	public ITerminableFuture<String> getResult(long delay);
	
//	/**
//	 *  Get the results.
//	 *  @param delay The delay that is waited between intermediate results.
//	 *  @param max The number of intermediate results that will be returned.
//	 *  @return The results.
//	 */
//	public ITerminableIntermediateFuture<String> getResults(long delay, int max);
}
