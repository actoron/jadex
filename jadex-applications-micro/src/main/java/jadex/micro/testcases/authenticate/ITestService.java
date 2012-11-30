package jadex.micro.testcases.authenticate;

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
//	@Autenticated("Lars-PC")
	public IFuture<Void> method(String msg);
}
