package jadex.webservice.examples.rs.banking;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.ResourceConfig;

/**
 * 
 */
@Path("/banking")
//Jadex service can be fetched via url from properties
//String url = uriinfo.getBaseUri().toString();
//IBankingService bs = (IBankingService)rc.getProperties().get(url);
public class RSBankingService
{
	@Context 
	public ResourceConfig rc;
	
	@Context
	public UriInfo uriinfo;

	/**
	 *  Get the account statement.
	 *  @param request The request.
	 *  @return The account statement.
	 */
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String getServiceInfo()
	{
		return "<html><body></body><h1>info</h1></html>";
	}
}
