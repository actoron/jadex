package jadex.micro.testcases.semiautomatic.servicevalue;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Interface of a news service.
 */
@Service
public interface INewsService
{
	/**
	 *  Subscribe to the newsprovider.
	 */
	public ISubscriptionIntermediateFuture<String> subscribeToNews();
}
