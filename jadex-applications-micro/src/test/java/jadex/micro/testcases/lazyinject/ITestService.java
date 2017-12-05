package jadex.micro.testcases.lazyinject;

import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITuple2Future;

/**
 *  Simple test service for tuple/intermediate future results.
 */
public interface ITestService
{
	/**
	 *  Method that returns a tuple2 future.
	 */
	public ITuple2Future<String, Integer> getFirstTupleResult();

	/**
	 *  Method that returns an intermediate future.
	 */
	public IIntermediateFuture<String> getIntermediateResults();
}
