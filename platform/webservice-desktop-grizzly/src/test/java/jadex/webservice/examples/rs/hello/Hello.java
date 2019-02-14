package jadex.webservice.examples.rs.hello;

import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.server.ResourceConfig;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.service.BasicService;
import jadex.extension.rs.publish.JadexXMLBodyReader;
import jadex.xml.bean.JavaWriter;

@Path("")
public class Hello extends BasicService
{
	public Hello() 
	{
		super(new ComponentIdentifier(), IHelloService.class, null);
	}
	
	@Context 
	public ResourceConfig rc;
	
	@Context 
	public UriInfo context;
	
    @Context
    public Request request;
	
//    @Path("sayPlainTextHello")
//	@GET
//	@Produces(MediaType.TEXT_PLAIN)
	public String sayPlainTextHello()
	{
//		System.out.println("hi: "+rc.getProperties().get("hallo"));
		return "Hello Jersey";
	}
	
//    @Path("sayXMLHello")
//	@GET
//	@Produces(MediaType.TEXT_XML)
	public String sayXMLHello()
	{
		return JavaWriter.objectToXML("Hello Jersey", null);
	}
	
//    @Path("sayHTMLHello")
//	@GET
//	@Produces(MediaType.TEXT_HTML)
	public String sayHTMLHello()
	{
		return "<html><body><h1>Hello Jersey</h1></body></html>";
	}

//	@GET
//	@Path("getXML/{request}")
//	@Produces(MediaType.APPLICATION_XML)
//	public String getXML(@PathParam("request") Request request)
//	{
//		System.out.println("getXML");
//		return "yes";
//	}
	
//	@GET
//	@Path("getJSON/{request}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public String getJSON(@PathParam("request") A a)
//	{
//		System.out.println("getJSON");
//		return "yes";
//	}
	
//	@POST
//	@Path("getJSON")
//	@Consumes(MediaType.APPLICATION_XML)
//	@Produces(MediaType.TEXT_PLAIN)
//	public String getJSON2(@FormParam("request") String a)
//	{
//		System.out.println("getJSON: "+a);
//		A ao = JavaReader.objectFromXML(a, null);
//		return "yes";
//	}
	
//	@POST
//	@Path("getJSON")
//	@Consumes(MediaType.APPLICATION_JSON)
//	@Produces(MediaType.TEXT_PLAIN)
//	public String getJSON2(A a)
//	{
//		System.out.println("getJSON: "+a);//+" "+aa);
//		System.out.println("getJSON: "+context.getQueryParameters());
////		A ao = JavaReader.objectFromXML(a, null);
//		return "yes";
//	}
//	
//	@GET
//	@Path("getXML")
//	@Produces(MediaType.APPLICATION_XML)
//	public A getXML()
//	{
//		System.out.println("getXML"+context.getQueryParameters());
//		return new A("hallo");
//	}
		
	
//	/**
//	 *  Main for testing.
//	 */
//	public static void main(String[] args)
//	{
//		try
//		{
////			URI uri = UriBuilder.fromUri("http://localhost/").port(8080).build();
//			URI uri = new URI("http://localhost:8080/");
//			
//			Map<String, Object> props = new HashMap<String, Object>();
//			props.put("com.sun.jersey.config.property.packages", "jadex.webservice.examples.rs.hello, jadex.extension.rs.publish");
//			props.put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//			props.put("hallo", "hallo");
//			// "com.sun.jersey.config.property.packages", 
//			PackagesResourceConfig config = new PackagesResourceConfig(props);
////			PackagesResourceConfig config = new PackagesResourceConfig(new String[]{"jadex.micro.examples.rs.banking"});
////			config.setPropertiesAndFeatures(props);
//			
//			HttpServer srv = GrizzlyServerFactory.createHttpServer(uri, config);
//			
//			srv.start();
//			
//			ClientConfig cc = new DefaultClientConfig();
////			cc.getClasses().add(JAXBProvider.class);
//			cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
//			cc.getClasses().add(JadexXMLBodyReader.class);
//			Client client = Client.create(cc);
//			WebResource service = client.resource(uri); 
////			String res = service.path("hello").accept(MediaType.TEXT_PLAIN).get(String.class);
////			String res = service.path("banking").accept(MediaType.TEXT_HTML).get(String.class);
////			System.out.println("Received: "+res);
//			
//			A a = new A("a");
//			a.getB().add("aa");
////			a.setB(new String[]{"aa"});
//			
//			ObjectMapper mapper = new ObjectMapper();
//			
//			// Form parameter can only be of type string 
////			String res = service.path("hello").
//			Form f = new Form();
////			f.add("a", JavaWriter.objectToXML(a, null));
//			System.out.println("jackson json: "+mapper.writeValueAsString(a));
//			
////			Class[] types = {A.class};
////			JSONJAXBContext contextj = new JSONJAXBContext(JSONConfiguration.mapped().build(), types);
////			JSONMarshaller ma = contextj.createJSONMarshaller();
////			StringWriter sw = new StringWriter();
////			ma.marshallToJSON(a, sw);
////			System.out.println("jaxb json: "+sw.toString());
//			
//			// .accept(MediaType.APPLICATION_JSON)
////			service.path("hello/getJSON").type(MediaType.APPLICATION_JSON).post(String.class, a);
//			A res = service.path("hello/getXML").type(MediaType.APPLICATION_XML).get(A.class);
//			System.out.println("got: "+res);
////			String res = service.path("hello/getJSON").type(MediaType.APPLICATION_XML).post(String.class, f);
//			
////			JAXBContext context = JAXBContext.newInstance(A.class);
////			Marshaller m = context.createMarshaller();
////			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
////			m.setProperty(Marshaller.JAXB_ENCODING, Marshaller.)
////			m.marshal(a, System.out);
//			
////			response = service.path("rest").path("todos").type(MediaType.APPLICATION_FORM_URLENCODED)
////			   .post(ClientResponse.class, form);
////			System.out.println("Received: "+res);
//
//			System.in.read();
//			srv.stop();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		try
		{
			ClientConfig cc = new ClientConfig();
//			cc.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			cc.register(JadexXMLBodyReader.class);
			Client client = ClientBuilder.newClient(cc);
			WebTarget service = client.target("http://localhost:8080/banking1/"); 
//			ObjectMapper mapper = new ObjectMapper();
//			System.out.println("jackson json: "+mapper.writeValueAsString(null));
//			service.path("addTransactionDataJSON").request(MediaType.TEXT_PLAIN).post(Entity.text("hallo"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
//	/**
//	 *  Main for testing.
//	 */
//	public static void main(String[] args)
//	{
//		try
//		{
//			URI uri = new URI("http://localhost:8080/banking");
//			
//			Map<String, Object> props = new HashMap<String, Object>();
//			props.put("com.sun.jersey.config.property.packages", "examples.rs.hello");
//			PackagesResourceConfig config = new PackagesResourceConfig(props);
//			HttpServer srv = GrizzlyServerFactory.createHttpServer(uri, config);
//			
//			srv.start();
//			
//			System.in.read();
//			srv.stop();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
//	}
	
}

