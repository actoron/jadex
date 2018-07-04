package jadex.webservice.examples.rs.hello;
import static org.junit.Assert.assertEquals;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.SReflect;
import jadex.commons.future.IFuture;

import java.net.BindException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.ClassNamesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;


/**
 * Test the Consuming part of the Jadex Rest Webservice Extension.
 * Creates a Simple Rest Service and tries to access it via 
 * a Jadex Service.
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
@Ignore	// TODO: HelloProvider.component.xml not found due to resources not copied from java src dir?
public class RSHelloTest
{

	private static final String BASE_URI = "http://localhost";
	private int basePort = 9123;
	private Hello hello;
	private Object httpServer;
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

    @Before
	public void setUp() throws Exception
	{
		new SReflectSub().setIsAndroid(true, true);
		
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

		IPlatformConfiguration config = PlatformConfigurationHandler.getMinimal();
		config.setKernels(IPlatformConfiguration.KERNEL_COMPONENT, IPlatformConfiguration.KERNEL_MICRO);
		config.getExtendedPlatformConfiguration().setTcpTransport(false);
		config.addComponent("jadex.webservice.examples.rs.hello.HelloProvider.component.xml");
		IFuture<IExternalAccess> fut = Starter.createPlatform(config);

//		IFuture<IExternalAccess> fut = Starter.createPlatform(new String[]
//		{"-gui", "false", "-awareness", "false", "-relaytransport", "false", "-tcptransport", "false",
//				"-component", "jadex/webservice/examples/rs/hello/HelloProvider.component.xml"});

		extAcc = fut.get();
	}

    @After
	public void tearDown() throws Exception
	{
//		pservice.unpublishService(sid);
		httpServer.getClass().getMethod("stop").invoke(httpServer); // references to HttpServer not working because of conflicting classloaders (robolectric/AppLoader)
	}

    @Test
	public void testAccessRestService() throws InterruptedException
	{
		IFuture<IHelloService> fut = SServiceProvider.searchService(extAcc, new ServiceQuery<>( IHelloService.class, RequiredServiceInfo.SCOPE_PLATFORM));

		IHelloService hs = fut.get();
		
		String xmlHello = hs.getXMLHello().get();

		assertEquals(hello.sayXMLHello(), xmlHello);
		System.out.println("Response: " + xmlHello);
	}

	private class SReflectSub extends SReflect {
		public void setIsAndroid(Boolean isAndroidFlag, Boolean isAndroidTestingFlag) {
			SReflect.setAndroid(isAndroidFlag, isAndroidTestingFlag);
		}
	}
}
