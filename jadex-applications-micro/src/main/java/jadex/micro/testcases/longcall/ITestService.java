package jadex.micro.testcases.longcall;

import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Test interface that has a timeout annotation specifying
 *  the default timeout. In the test the timeout is provided
 *  by the caller via the non-functional properties in the
 *  ServiceCall object (CallLocal).
 */
public interface ITestService
{
	/**
	 *  A first test method.
	 */
	@Timeout(2000)
	public IFuture<Void> method(String msg);
	
	/**
	 *  A second test method.
	 */
	@Timeout(2000)
	public IIntermediateFuture<Void> imethod();
}
