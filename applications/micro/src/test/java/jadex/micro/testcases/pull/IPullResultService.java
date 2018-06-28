package jadex.micro.testcases.pull;

import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.IPullSubscriptionIntermediateFuture;

/**
 *  Simple intermediate service interface.
 */
public interface IPullResultService 
{
	/**
	 *  Get the results.
	 *  @param max The number of intermediate results that will be returned.
	 *  @return The results.
	 */
	public IPullIntermediateFuture<String> getResultsA(int max);
	
	/**
	 *  Get the results.
	 *  @param max The number of intermediate results that will be returned.
	 *  @return The results.
	 */
	public IPullSubscriptionIntermediateFuture<String> getResultsB(int max);
}
