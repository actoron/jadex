package jadex.bridge.service.types.email;

import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  The email service allows for sending and receiving emails.
 */
public interface IEmailService
{
	/**
	 *  Send an email.
	 *  @param email The email.
	 *  @param account The email account.
	 */
	public IFuture<Void> sendEmail(@CheckNotNull Email email, EmailAccount account);

	/**
	 *  Subscribe for email.
	 *  @param filter The filter.
	 *  @param account The email account.
	 */
	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<Email> subscribeForEmail(IFilter<Email> filter, EmailAccount account);
}
