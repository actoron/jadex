package jadex.micro.testcases.remotestepinservicecall;

import jadex.bridge.IExternalAccess;
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
	public IFuture<Void> method(IExternalAccess exta);
}
