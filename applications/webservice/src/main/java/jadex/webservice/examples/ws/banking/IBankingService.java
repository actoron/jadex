package jadex.webservice.examples.ws.banking;

import jadex.commons.future.IFuture;


/**
 *  The Jadex asynchronous banking service. 
 */
public interface IBankingService
{
	/**
	 *  Get the account statement.
	 *  @param request The request.
	 *  @return The account statement.
	 */
	public IFuture<AccountStatement> getAccountStatement(Request request);
}
