package jadex.webservice.examples.rs.banking;

import jadex.commons.future.ThreadSuspendable;
import jadex.extension.rs.publish.DefaultRestServicePublishService;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.api.core.ResourceConfig;

/**
 *  Rest service implementation that provides some methods by itself.
 *  Using the 'generate=true' option the missing interface methods
 *  will be automatically added by Jadex Rest publishing. 
 */
@Path("/")
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
	
	/**
	 *  Get the account statement.
	 *  @param request The request.
	 *  @return The account statement.
	 */
	@GET
	@Path("getAccountStatement")
	@Produces(MediaType.APPLICATION_XML)
	public String getAccountStatement(@QueryParam("request") String request)
	{
		System.out.println("getAccountStatement: "+request);
		
		//Jadex service can be fetched from properties
		IBankingService bs = (IBankingService)rc.getProperties().get(DefaultRestServicePublishService.JADEXSERVICE);
		Request req;
		try
		{
			req = JavaReader.objectFromXML(request, null);
		}
		catch(Exception e)
		{
			req = new Request(new Date(), new Date());
		}
		AccountStatement ret = bs.getAccountStatement(req).get(new ThreadSuspendable(this));
		
		return JavaWriter.objectToXML(ret, null);
	}
}
