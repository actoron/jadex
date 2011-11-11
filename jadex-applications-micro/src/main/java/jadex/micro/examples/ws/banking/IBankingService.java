package jadex.micro.examples.ws.banking;

import jadex.commons.future.IFuture;


/**
 *  
 */
public interface IBankingService
{
	/**
	 *  Get a quote.
	 */
	public IFuture<AccountStatement> getAccountStatement(Request request);
}
