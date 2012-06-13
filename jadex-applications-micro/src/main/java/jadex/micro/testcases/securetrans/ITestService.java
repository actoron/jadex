package jadex.micro.testcases.securetrans;

import jadex.bridge.service.annotation.SecureTransmission;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ITestService
{
	/**
	 * 
	 */
	@SecureTransmission
	public IFuture<Void> secMethod(String msg);
	
	/**
	 * 
	 */
	public IFuture<Void> unsecMethod(String msg);
}
