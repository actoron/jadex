package jadex.micro.testcases.subscriptionlistener;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.ISubscriptionIntermediateFuture;

@Service
public interface ITestService
{
	/**
	 * 
	 */
	public ISubscriptionIntermediateFuture<String> test();
}
