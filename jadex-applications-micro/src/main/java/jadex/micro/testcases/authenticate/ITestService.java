package jadex.micro.testcases.authenticate;

import jadex.bridge.service.annotation.Authenticated;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ITestService
{
	/**
	 * 
	 */
	@Authenticated
	public IFuture<Void> method(String msg);
}
