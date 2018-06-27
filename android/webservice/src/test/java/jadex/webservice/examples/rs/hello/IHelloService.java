package jadex.webservice.examples.rs.hello;

import jadex.commons.future.IFuture;


/**
 *  The Jadex asynchronous banking service. 
 */
public interface IHelloService
{
	/**
	 *  Get the account statement.
	 *  @param request The request.
	 *  @return The account statement.
	 */
	public IFuture<String> getXMLHello();

}
