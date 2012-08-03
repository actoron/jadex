package jadex.webservice.examples.rs.hello;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

import java.net.BindException;

import junit.framework.TestCase;

import org.glassfish.grizzly.http.server.HttpServer;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

/**
 * Test the Consuming part of the Jadex Rest Webservice Extension.
 * Creates a Simple Rest Service and tries to access it via 
 * a Jadex Service.
 *
 */
public class RSHelloTest extends TestCase
{

	private static final String BASE_URI = "http://localhost";
	private int basePort = 9123;
	private Hello hello;
	private HttpServer httpServer;
	private IExternalAccess extAcc;

	protected void setUp() throws Exception
	{
		hello = new Hello();
		System.out.println("Starting grizzly...");
		ResourceConfig rc;

		rc = new ClassNamesResourceConfig(Hello.class);
		for (int i = 0; i < 20; i++)
		{
			try
			{
				httpServer = GrizzlyServerFactory.createHttpServer(BASE_URI + ":" + basePort, rc);
				break;
			}
			catch (BindException e)
			{
				System.out.println("Port in use: " + basePort + ", trying another port ...");
				basePort += 21;
			}
		}

		httpServer.start();

		IFuture<IExternalAccess> fut = Starter.createPlatform(new String[]
		{"-gui", "false", "-awareness", "false", "-relaytransport", "false", "-tcptransport", "false", "-componentfactory",
				"jadex.component.ComponentComponentFactory", "-conf", "jadex/standalone/Platform.component.xml", "-component",
				"jadex/webservice/examples/rs/hello/HelloProvider.component.xml"});

		ThreadSuspendable sus = new ThreadSuspendable();
		extAcc = fut.get(sus);
	}

	protected void tearDown() throws Exception
	{
		httpServer.stop();
	}

	public void testAccessRestService()
	{
		ThreadSuspendable sus = new ThreadSuspendable();

		IFuture<IHelloService> fut = SServiceProvider.getService(extAcc.getServiceProvider(), IHelloService.class);

		IHelloService hs = fut.get(sus);

		String xmlHello = hs.getXMLHello().get(sus);

		assertEquals(hello.sayXMLHello(), xmlHello);
		System.out.println("Response: " + xmlHello);
	}
}
