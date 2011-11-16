package jadex.micro.examples.ws.banking;

import javax.jws.WebService;



/**
 * 
 */
public interface IWSBankingService
{
	/**
	 *  Get a quote.
	 */
	public AccountStatement getAccountStatement(Request request);
}
