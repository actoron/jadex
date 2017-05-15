package jadex.webservice.examples.rs.hello;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.IFuture;
import jadex.extension.rs.publish.GrizzlyRestServicePublishService;

/**
 *  Test the consuming part of the Jadex Rest Webservice Extension.
 *  Creates a Simple Rest Service and tries to access it via 
 *  a Jadex Service.
 */
public class RSHelloTest //extends TestCase
{

//	private static final String BASE_URI = "http://localhost";
//	private int basePort = 9123;
	private Hello hello;
//	private HttpServer httpServer;
	private IExternalAccess extAcc;
	private GrizzlyRestServicePublishService pservice;
	private IServiceIdentifier sid;
	
	@Before
	public void setUp() throws Exception
	{
		hello = new Hello();
		hello.createServiceIdentifier("hello", Hello.class, null, Hello.class, null);
		sid	= hello.getServiceIdentifier();
		
		pservice = new GrizzlyRestServicePublishService();
		// Grizzly breaks without trailing '/murks' !?
		PublishInfo pi = new PublishInfo("http://localhost:9123/murks", "", IRSHelloService.class);
		pi.addProperty("generate", "false");
//		
		IFuture<Void> publishService = pservice.publishService(hello.getServiceIdentifier(), pi);
//		
//		ThreadSuspendable sus = new ThreadSuspendable();
		publishService.get();

		IFuture<IExternalAccess> fut = Starter.createPlatform(new String[]
		{"-gui", "false", "-awareness", "false", "-relaytransport", "false", "-tcptransport", "false",
//				"-componentfactory", "jadex.component.ComponentComponentFactory",
//				"-conf", "jadex/platform/Platform.component.xml",
				"-component", "jadex/webservice/examples/rs/hello/HelloProvider.component.xml"});

		extAcc = fut.get();
	}

//	protected void setUp() throws Exception
//	{
//		hello = new Hello();
//		System.out.println("Starting grizzly...");
//		ResourceConfig rc;
//
//		rc = new ClassNamesResourceConfig(Hello.class);
//	
//		
//		for (int i = 0; i < 20; i++)
//		{
//			try
//			{
//				httpServer = GrizzlyServerFactory.createHttpServer(BASE_URI + ":" + basePort, rc);
//				break;
//			}
//			catch (BindException e)
//			{
//				System.out.println("Port in use: " + basePort + ", trying another port ...");
//				basePort += 21;
//			}
//		}
//
//		httpServer.start();
//
//		IFuture<IExternalAccess> fut = Starter.createPlatform(new String[]
//		{"-gui", "false", "-awareness", "false", "-relaytransport", "false", "-tcptransport", "false",
////				"-componentfactory", "jadex.component.ComponentComponentFactory",
////				"-conf", "jadex/platform/Platform.component.xml",
//				"-component", "jadex/webservice/examples/rs/hello/HelloProvider.component.xml"});
//
//		ThreadSuspendable sus = new ThreadSuspendable();
//		extAcc = fut.get(sus);
//	}

	@After
	public void tearDown() throws Exception
	{
		pservice.unpublishService(sid);
	}

	// Test that runs service raw without component then expects them to be published wtf?
	//@Test
	public void testAccessRestService()
	{
		try
		{
//		ThreadSuspendable sus = new ThreadSuspendable();

		IFuture<IHelloService> fut = SServiceProvider.getService(extAcc, IHelloService.class, RequiredServiceInfo.SCOPE_PLATFORM);

		IHelloService hs = fut.get();

		String xmlHello = hs.sayXMLHello().get();

		Assert.assertEquals(hello.sayXMLHello(), xmlHello);
		System.out.println("Response: " + xmlHello);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
