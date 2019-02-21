package jadex.webservice.examples.rs.hello;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.service.BasicService;
import jadex.xml.bean.JavaWriter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;

@Path("")
public class Hello extends BasicService
{
	public Hello() {
		super(new ComponentIdentifier(), IHelloService.class, null);
	}
	
	@Context 
	public ResourceConfig rc;
	
	@Context 
	public UriInfo context;
	
    @Context
    public Request request;
	
    @Path("sayPlainTextHello")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello()
	{
//		System.out.println("hi: "+rc.getProperties().get("hallo"));
		return "Hello Jersey";
	}
	
    @Path("sayXMLHello")
	@GET
	@Produces(MediaType.TEXT_XML)
	public String sayXMLHello()
	{
		return JavaWriter.objectToXML("Hello Jersey", null);
	}
	
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String sayHTMLHello()
	{
		return "<html><body><h1>Hello Jersey</h1></body></html>";
	}

}

