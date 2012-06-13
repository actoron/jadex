package jadex.micro.testcases.securetrans;

import jadex.bridge.service.annotation.SecureTransmission;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ITestService
{
	/**
	 *  Call a method that must use a secure
	 *  transport under the hood.
	 */
	@SecureTransmission
	public IFuture<Void> secMethod(String msg);
	
	/**
	 *  Call a method that can use any transport.
	 */
	public IFuture<Void> unsecMethod(String msg);
}
