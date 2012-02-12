package jadex.webservice.examples.rs.banking;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The Jadex implementation of the banking service.
 */
@Service
public class BankingService implements IBankingService
{
	/** The account data. */
	protected List<String> data;
	
	/**
	 *  Init with some data.
	 */
	@ServiceStart
	public void start()
	{
		data = new ArrayList<String>();
		data.add("Statement 1");
		data.add("Statement 2");
		data.add("Statement 3");
	}
	
	/**
	 *  Get the account statement.
	 *  @param request The request.
	 *  @return The account statement.
	 */
	public IFuture<AccountStatement> getAccountStatement(Date begin, Date end)
	{
		System.out.println("getAccountStatement(Date begin, Date end)");
		AccountStatement as = new AccountStatement(data.toArray(new String[data.size()]), new Request(begin, end));
		return new Future<AccountStatement>(as);
	}

	
	/**
	 *  Get account statement.
	 *  @return The account statement.
	 */
	public IFuture<AccountStatement> getAccountStatement()
	{
		System.out.println("getAccountStatement()");
		AccountStatement as = new AccountStatement(data.toArray(new String[data.size()]), null);
		return new Future<AccountStatement>(as);
	}
	
	/**
	 *  Get an account statement.
	 *  @param request The request.
	 *  @return The account statement.
	 */
	public IFuture<AccountStatement> getAccountStatement(Request request)
	{
		System.out.println("getAccountStatement(Request request)");
		AccountStatement as = new AccountStatement(data.toArray(new String[data.size()]), request);
		return new Future<AccountStatement>(as);
	}
	
	/**
	 *  Add an account statement.
	 *  @param data The data.
	 */
	public IFuture<Void> addTransactionData(String data)
	{
		System.out.println("addTransactionData(String data)");
		this.data.add(data);
		return IFuture.DONE;
	}
	
	/**
	 *  Remove an account statement.
	 *  @param data The data.
	 */
	public IFuture<Void> removeTransactionData(String data)
	{
		System.out.println("removeTransactionData(String data)");
		this.data.remove(data);
		return IFuture.DONE;
	}
}
