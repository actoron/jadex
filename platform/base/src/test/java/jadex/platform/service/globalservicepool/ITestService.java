package jadex.platform.service.globalservicepool;

import jadex.commons.future.IFuture;

/**
 *
 */
public interface ITestService
{
	/**
	 *  A test method.
	 */
	public IFuture<Void> methodA(int cnt);
}
