package jadex.webservice.examples.rs.banking;

import jadex.commons.future.IFuture;


/**
 *  The Jadex asynchronous banking service. 
 */
public interface IBankingService
{
	/**
	 *  Get all account statements.
	 *  @return The account statements.
	 */
	public IFuture<AccountStatement> getAccountStatement();
	
	/**
	 *  Get the account statement.
	 *  @param request The request.
	 *  @return The account statement.
	 */
	public IFuture<AccountStatement> getAccountStatement(Request request);
	
	/**
	 *  Add an account statement.
	 *  @param data The data.
	 */
	public IFuture<Void> addTransactionData(String data);
	
	/**
	 *  Remove an account statement.
	 *  @param data The data.
	 */
	public IFuture<Void> removeTransactionData(String data);
}
