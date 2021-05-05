package jadex.tools.web.legacystatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Scanner;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.component.IMessageFeature;

/**
 *  Test the web registry page.
 */
public class StatusWebGuiTest
{
	@Test
	public void	testStatusPage() throws IOException
	{
		// Find free port for http publishing
		ServerSocket	s	= new ServerSocket(0);
		int port	= s.getLocalPort();
		s.close();
		
		// Start platform with published status agent gui
		IPlatformConfiguration	baseconf	= STest.createDefaultTestConfig(getClass());
		IPlatformConfiguration	webguiconf	= baseconf
			.setSuperpeer(true)
			.getExtendedPlatformConfiguration().setRsPublish(true)
			.setValue("jettyrspublish", true)
			.setValue("status", true)
			.setValue("status.port", port);
//			config.setLogging(true);
//			config.setValue("nanorspublish", false);
		
		IExternalAccess	platform	= Starter.createPlatform(webguiconf).get();
		
		// Check that index.html is correctly served.
		String	file	= getUrlContent(getClass().getResource("/META-INF/legacystatuswebgui/index.html"));
		String	http	= null;
		for(int i=0; i<10 && http==null; i++)
		{
			try
			{
				http	= getUrlContent(new URL("http://localhost:"+port+"/"));
			}
			catch(IOException e)
			{
				if(i==9)
				{
					throw e;
				}
				// Wait for publish service (hack?)
				platform.waitForDelay(Starter.getScaledDefaultTimeout(platform.getId(), 0.1));				
			}
		}
		assertEquals(file, http);
		
		// Start second platform and send message so that one connection exists
		IExternalAccess	dummy	= Starter.createPlatform(baseconf).get();
		dummy.scheduleStep(ia -> ia.getFeature(IMessageFeature.class).sendMessage("huhu", platform.getId())).get();
		// Check that platforms can be retrieved.
//		String	con	= getUrlContent(new URL("http://localhost:"+port+"/status/subscribeToConnections"));	// TODO: why empty initial response and how to handle (x-jadex-callid...)
		String	con	= getUrlContent(new URL("http://localhost:"+port+"/status/getConnectedPlatforms"));
		System.out.println("platform: "+con);
		assertContainsField(con, "platform");
		assertContainsField(con, "connected");
		assertContainsField(con, "protocol");
		dummy.killComponent().get();

		// Check that provided services can be retrieved.
		String	service	= getUrlContent(new URL("http://localhost:"+port+"/status/subscribeToServices"));
		System.out.println("service: "+service);
		assertContainsField(service, "type");
		assertContainsField(service, "providerId");
		assertContainsField(service, "tags");
		assertContainsField(service, "scope");
		assertContainsField(service, "networkNames");
		assertContainsField(service, "unrestricted");

		// Check that queries can be retrieved.
		String	query	= getUrlContent(new URL("http://localhost:"+port+"/status/subscribeToQueries"));
		System.out.println("query: "+query);
		assertContainsField(query, "serviceType");
		assertContainsField(query, "owner");
		assertContainsField(query, "scope");

		platform.killComponent().get();
	}

	//-------- helper methods --------
	
	/**
	 *  Retrieve URL content and return as string (UTF-8).
	 */
	protected String getUrlContent(URL url) throws IOException
	{
		try(InputStream	is	= url.openStream())
		{
			try(Scanner	s	= new Scanner(is, "UTF-8"))
			{
				return s.useDelimiter("\\A").next();
			}
		}
	}
	
	/**
	 *  Check that the JSON string contains a given field.
	 */
	protected void assertContainsField(String json, String field)
	{
		assertTrue("JSON misses field \""+field+"\": "+json, json.indexOf("\""+field+"\":")!=-1);
	}
}
