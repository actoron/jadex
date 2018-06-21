package jadex.micro.testcases.nfcallreturn;

import jadex.commons.future.IFuture;

/**
 *  Test interface that has a timeout annotation specifying
 *  the default timeout. In the test the timeout is provided
 *  by the caller via the non-functional properties in the
 *  ServiceCall object (CallLocal).
 */
public interface ITestService
{
	/**
	 *  A test method.
	 */
//	@Timeout(10000)
	public IFuture<Void> method(String msg);
}
