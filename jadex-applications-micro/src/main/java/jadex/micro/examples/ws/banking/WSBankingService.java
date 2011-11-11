package jadex.micro.examples.ws.banking;

import jadex.commons.future.ThreadSuspendable;

import javax.jws.WebService;

@WebService
public class WSBankingService implements IWSBankingService
{
	/** The original service. */
	protected IBankingService qs;
	
	
	/**
	 * 
	 */
	public WSBankingService()
	{
	}
	
	/**
	 * 
	 */
	public WSBankingService(IBankingService qs)
	{
		this.qs = qs;
	}
	
	/**
	 *  Get a quote.
	 */
	public AccountStatement getAccountStatement(Request request)
	{
		return qs.getAccountStatement(request).get(new ThreadSuspendable());
	}
}