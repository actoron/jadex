package jadex.webservice.examples.rs.banking;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;

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
	
	/** The component. */
	@ServiceComponent
	protected IInternalAccess component;
	
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
	 *  Subscribe for account statements.
	 *  @return Account statements whenever available.
	 */
	public IIntermediateFuture<AccountStatement> subscribeForAccountStatements()
	{
		final IntermediateFuture<AccountStatement> ret = new IntermediateFuture<AccountStatement>();
		
		
		final int max = 5;
		ret.addIntermediateResult(new AccountStatement(new String[]{"initial"}, null));
		component.getComponentFeature(IExecutionFeature.class).waitForDelay(1500, new IComponentStep<Void>()
		{
			int cnt = 0;
			public IFuture<Void> execute(IInternalAccess ia)
			{
				if(cnt++<max)
				{
					ret.addIntermediateResult(new AccountStatement(new String[]{""+cnt}, null));
					component.getComponentFeature(IExecutionFeature.class).waitForDelay(500, this);
				}
				else
				{
					ret.setFinished();
				}
				return IFuture.DONE;
			}
		});
		
		return ret;
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
