package jadex.webservice.examples.rs.banking;

import java.util.Arrays;

//import org.glassfish.grizzly.http.server.HttpServer;
//import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
//import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
//import org.glassfish.jersey.server.ResourceConfig;

/**
 *  Account statement class.
 */
public class AccountStatement
{
	//-------- attributes --------
	
	/** The account data. */
	protected String[] data;
	
	/** The request. */
	protected Request request;
	
	//-------- constructors --------
	
	/**
	 *  Create an account statement.
	 */
	public AccountStatement()
	{
	}

	/**
	 *  Create an account statement.
	 */
	public AccountStatement(String[] data, Request request)
	{
		this.data = data;
		this.request = request;
	}

	//-------- methods --------
	
	/**
	 *  Get the data.
	 *  @return the data.
	 */
	public String[] getData()
	{
		return data;
	}

	/**
	 *  Set the data.
	 *  @param data The data to set.
	 */
	public void setData(String[] data)
	{
		this.data = data;
	}

	/**
	 *  Get the request.
	 *  @return the request.
	 */
	public Request getRequest()
	{
		return request;
	}

	/**
	 *  Set the request.
	 *  @param request The request to set.
	 */
	public void setRequest(Request request)
	{
		this.request = request;
	}
	
	public String toString()
	{
		return "AccountStatement [data=" + Arrays.toString(data) +"]";
	}

//	public static void main(String[] args) 
//    {
//        try 
//        {
//            System.out.println("JSON with MOXy Jersey Example App");
//
//            MoxyJsonConfig moxyJsonConfig = new MoxyJsonConfig();
//            Map<String, String> namespacePrefixMapper = new HashMap<String, String>(1);
//            namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
//            moxyJsonConfig.setNamespacePrefixMapper(namespacePrefixMapper).setNamespaceSeparator(':');
//            
//            ResourceConfig rc = new ResourceConfig().packages("jadex.webservice.examples.rs.banking").
//            	register(moxyJsonConfig.resolver());
//            
//            HttpServer server = GrizzlyHttpServerFactory.createHttpServer(new URI("http://localhost:9998/jsonmoxy/"), rc);
//
//            System.out.println(String.format("Application started.%nHit enter to stop it..."));
//            System.in.read();
//            server.shutdownNow();
//        } 
//        catch(Exception e) 
//        {
//        	e.printStackTrace();
//        }
//    }

}
