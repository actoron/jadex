package jadex.micro.testcases.intermediate;

import jadex.commons.future.IIntermediateFuture;

/**
 *  Simple intermediate service interface.
 */
public interface IIntermediateResultService 
{
	/**
	 *  Get the results.
	 *  @param delay The delay that is waited between intermediate results.
	 *  @param max The number of intermediate results that will be returned.
	 *  @return The results.
	 */
	public IIntermediateFuture<String> getResults(long delay, int max);
}
