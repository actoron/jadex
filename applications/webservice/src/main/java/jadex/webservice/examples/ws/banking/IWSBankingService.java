package jadex.webservice.examples.ws.banking;

/**
 *  Interface that is used as web service.
 */
public interface IWSBankingService
{
	/**
	 *  Get an account statement.
	 *  @param request The request.
	 *  @return The account statement.
	 */
	public AccountStatement getAccountStatement(Request request);
}
