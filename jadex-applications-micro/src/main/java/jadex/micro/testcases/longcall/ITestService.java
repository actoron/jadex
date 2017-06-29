package jadex.micro.testcases.longcall;

import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.IPullSubscriptionIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;

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
	@Timeout(100)
	public IFuture<Void> method1();
	
	/**
	 *  A test method.
	 */
	@Timeout(100)
	public ITerminableFuture<Void> method2();
	
	/**
	 *  A test method.
	 */
	@Timeout(100)
	public IIntermediateFuture<Void> method3();
	
	/**
	 *  A test method.
	 */
	@Timeout(100)
	public ISubscriptionIntermediateFuture<Void> method4();
	
	/**
	 *  A test method.
	 */
	@Timeout(100)
	public IPullIntermediateFuture<Void> method5();
	
	/**
	 *  A test method.
	 */
	@Timeout(100)
	public IPullSubscriptionIntermediateFuture<Void> method6();

}
