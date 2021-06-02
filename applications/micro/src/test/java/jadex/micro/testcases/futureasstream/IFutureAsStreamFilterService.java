package jadex.micro.testcases.futureasstream;

import jadex.commons.future.IIntermediateFuture;

/**
 *  Test service providing filtered intermediate results.
 */
public interface IFutureAsStreamFilterService
{
	/**
	 *  Intermediate future for testing.
	 */
	public IIntermediateFuture<String> filterResults();
}
