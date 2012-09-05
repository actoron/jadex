package jadex.micro.testcases.timeout;

import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ITestService
{
	/**
	 * 
	 */
	@Timeout(10000)
	public IFuture<Void> method(String msg);
}
