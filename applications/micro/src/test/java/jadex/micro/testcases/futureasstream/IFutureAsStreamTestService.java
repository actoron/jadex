package jadex.micro.testcases.futureasstream;

import jadex.commons.future.IIntermediateFuture;

/**
 *  Test service for using an intermediate future as stream.
 */
public interface IFutureAsStreamTestService
{
	/**
	 *  Intermediate future for testing.
	 */
	public IIntermediateFuture<String> getSomeResults();
}
