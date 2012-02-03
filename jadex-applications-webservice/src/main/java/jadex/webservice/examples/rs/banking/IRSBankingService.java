package jadex.webservice.examples.rs.banking;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * 
 */
public interface IRSBankingService
{
	/**
	 *  Get the account statement.
	 *  @param request The request.
	 *  @return The account statement.
	 */
	@GET
	@Path("getAccountStatement/{request}")
	@Produces(MediaType.APPLICATION_XML)
	public String getAccountStatementXML(@PathParam("request") String request);
	
	/**
	 *  Get the account statement.
	 *  @param request The request.
	 *  @return The account statement.
	 */
	@GET
	@Path("getAccountStatement/{request}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAccountStatementJSON(@PathParam("request") String request);
	
//	
//	/**
//	 *  Add an account statement.
//	 *  @param data The data.
//	 */
//	@POST
//	@Path("addTransactionData")
//	public void addTransactionData(String data)
//	{
//		System.out.println("addTransactionData");	
//	}
//	
//	/**
//	 *  Remove an account statement.
//	 *  @param data The data.
//	 */
//	@DELETE
//	@Path("removeTransactionData")
//	public void removeTransactionData(String data)
//	{
//		System.out.println("removeTransactionData");	
//	}
}
