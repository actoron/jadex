package jadex.micro.testcases.intermediate;

import jadex.commons.future.IIntermediateFuture;

/**
 *  Simple intermediate service interface.
 */
public interface IIntermediateResultService 
{
	/**
	 *  Get the results.
	 *  @return The results.
	 */
	public IIntermediateFuture<String> getResults();
}
