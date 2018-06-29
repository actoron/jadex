package jadex.platform.service.email;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.email.Email;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Email fetching service.
 */
@Service
public interface IEmailFetcherService
{
	/**
	 *  Fetch emails for a subscription.
	 *  @param sub The subscription.
	 *  @return The emails.
	 */
	public IIntermediateFuture<Email> fetchEmails(SubscriptionInfo sub);
}
