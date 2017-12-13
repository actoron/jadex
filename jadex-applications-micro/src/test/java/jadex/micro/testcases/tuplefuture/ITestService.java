package jadex.micro.testcases.tuplefuture;

import jadex.commons.future.ITuple2Future;

/**
 *  Simple test service for tuple future results.
 */
public interface ITestService
{
	/**
	 *  Method that returns a tuple2 future.
	 */
	public ITuple2Future<String, Integer> getSomeResults();
//	public IIntermediateFuture<String> getSomeResults();
}
