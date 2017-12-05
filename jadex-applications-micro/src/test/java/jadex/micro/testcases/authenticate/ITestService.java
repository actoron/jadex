package jadex.micro.testcases.authenticate;

import jadex.bridge.service.annotation.Authenticated;
import jadex.commons.future.IFuture;

/**
 *  Test service interface for authentication.
 */
public interface ITestService
{
	/**
	 *  Method that can only be called by authenticated callers.
	 */
	@Authenticated
	public IFuture<Void> method(String msg);
}
