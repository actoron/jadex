package jadex.micro.examples.ws.banking;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 *
 */
@Service
public class BankingService implements IBankingService
{
	/**
	 *  Get an account statement.
	 */
	public IFuture<AccountStatement> getAccountStatement(Request request)
	{
		String[] data = new String[]{"Statement 1", "Statement 2", "Statement 3"};
		AccountStatement as = new AccountStatement(data, request);
		return new Future<AccountStatement>(as);
	}
}
