package jadex.micro.examples.ws.banking;

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
