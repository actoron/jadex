package jadex.webservice.examples.rs.banking;

import java.util.Date;

import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;


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
	public IFuture<AccountStatement> getAccountStatement(Date begin, Date end);

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
	 *  Subscribe for account statements.
	 *  @return Account statements whenever available.
	 */
	public IIntermediateFuture<AccountStatement> subscribeForAccountStatements(long delay, int max);
	
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
