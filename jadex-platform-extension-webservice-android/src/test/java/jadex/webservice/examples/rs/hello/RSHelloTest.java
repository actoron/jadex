package jadex.webservice.examples.rs.hello;
import jadex.base.Starter;
import jadex.bridge.ClassInfo;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.ServiceIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;

import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.TestCase;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.nio.transport.TCPNIOServerConnection;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.test.framework.JerseyTest;


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
//	private DefaultRestServicePublishService pservice;
	private IServiceIdentifier sid;
	
	
//	protected void setUp() throws Exception
//	{
//		hello = new Hello();
//		hello.createServiceIdentifier("hello", Hello.class, null, Hello.class);
//		
//		pservice = new DefaultRestServicePublishService();
//		PublishInfo pi = new PublishInfo("http://localhost:9123", "", Hello.class, null);
//		IFuture<Void> publishService = pservice.publishService(getClass().getClassLoader(), hello, pi);
//		ThreadSuspendable sus = new ThreadSuspendable();
//		publishService.get(sus);
//
//		IFuture<IExternalAccess> fut = Starter.createPlatform(new String[]
//		{"-gui", "false", "-awareness", "false", "-relaytransport", "false", "-tcptransport", "false",
//				"-component", "jadex/webservice/examples/rs/hello/HelloProvider.component.xml"});
//		extAcc = fut.get(sus);
//	}

	public void setUp() throws Exception
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


		IFuture<IExternalAccess> fut = Starter.createPlatform(new String[]
		{"-gui", "false", "-awareness", "false", "-relaytransport", "false", "-tcptransport", "false",
				"-component", "jadex/webservice/examples/rs/hello/HelloProvider.component.xml"});

		ThreadSuspendable sus = new ThreadSuspendable();
		extAcc = fut.get(sus);
	}

	public void tearDown() throws Exception
	{
//		pservice.unpublishService(sid);
		httpServer.stop();
	}

	public void testAccessRestService() throws InterruptedException
	{
		ThreadSuspendable sus = new ThreadSuspendable();

		IFuture<IHelloService> fut = SServiceProvider.getService(extAcc.getServiceProvider(), IHelloService.class);

		IHelloService hs = fut.get(sus);
		
		String xmlHello = hs.getXMLHello().get(sus);

		assertEquals(hello.sayXMLHello(), xmlHello);
		System.out.println("Response: " + xmlHello);
	}
}
