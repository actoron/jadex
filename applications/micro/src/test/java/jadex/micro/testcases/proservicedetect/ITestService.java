package jadex.micro.testcases.proservicedetect;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  A test service interface.
 */
@Service
public interface ITestService
{
	/**
	 *  A test method.
	 */
	public IFuture<Void> testMethod();
}
