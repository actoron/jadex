package jadex.micro.testcases.autoterminate;

import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Test automatic termination of subscriptions, when subscriber dies.
 */
public interface IAutoTerminateService
{
	/**
	 *  Test subscription.
	 */
	public ISubscriptionIntermediateFuture<String>	subscribe();
}
