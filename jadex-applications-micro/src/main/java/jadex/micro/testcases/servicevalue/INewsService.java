package jadex.micro.testcases.servicevalue;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 * 
 */
@Service
public interface INewsService
{
	/**
	 *  Subscribe to the newsprovider.
	 */
	public ISubscriptionIntermediateFuture<String> subscribeToNews();
}
