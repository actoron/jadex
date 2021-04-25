package jadex.micro.testcases.futureasstream;

import jadex.commons.future.IFuture;

/**
 *  Callback service to check that caller is responding and not blocked by java stream API. 
 */
public interface IFutureAsStreamCallbackService
{
	/**
	 *  Provide the results.
	 */
	public IFuture<String> getNextResult();
}
