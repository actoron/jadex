package jadex.tools.web.legacystatus;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.simulation.SSimulation;
import jadex.commons.future.Future;

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
		IPlatformConfiguration	baseconf	= STest.createDefaultTestConfig(getClass())
			.setValue("superpeerclient.awaonly", false);
		IPlatformConfiguration	webguiconf	= baseconf.clone()
			.setSuperpeer(true)
			.getExtendedPlatformConfiguration().setRsPublish(true)
			.setValue("jettyrspublish", true)
			.setValue("status", true)
			.setValue("status.port", port)
			.setValue("status.path", "sumsi")
//			.setLogging(true)
			;
//			config.setValue("nanorspublish", false);
		
		String	publishurl	= "http://localhost:"+port+"/sumsi/";
		
		STest.runSimLocked(webguiconf, ia0 ->
		{
			IExternalAccess	platform	= ia0.getExternalAccess();
			
			// Check that index.html is correctly served.
			String	fileurl	= getClass().getResource("/META-INF/legacystatuswebgui/index.html").toExternalForm();
			String	file	= getUrlContent(fileurl);
			String	http	= null;
			for(int i=0; i<10 && http==null; i++)
			{
				try
				{
					http	= getUrlContent(publishurl);
				}
				catch(Exception e)
				{
					if(i==9)
					{
						throw e;
					}
					// Wait for publish service (hack?)
					platform.waitForDelay(Starter.getScaledDefaultTimeout(platform.getId(), 0.1)).get();
				}
			}
			assertEquals(file, http);
			
			// Start second platform and send message so that one connection exists
			IExternalAccess	dummy	= Starter.createPlatform(baseconf).get();
			dummy.scheduleStep(ia -> ia.getFeature(IMessageFeature.class).sendMessage("huhu", platform.getId())).get();
			// Check that platforms can be retrieved.
			String	con	= getUrlContent(publishurl+"api/subscribeToConnections");
			System.out.println("platform: "+con);
			assertContainsField(con, "platform");
			assertContainsField(con, "connected");
			assertContainsField(con, "protocol");

			// Check that queries can be retrieved.
			dummy.addQuery(new ServiceQuery<>(IStatusService.class).setScope(ServiceScope.NETWORK));
			String	query	= getUrlContent(publishurl+"api/subscribeToQueries");
			System.out.println("query: "+query);
			assertContainsField(query, "serviceType");
			assertContainsField(query, "owner");
			assertContainsField(query, "scope");
			
			// Check that provided services can be retrieved.
			String	service	= getUrlContent(publishurl+"api/subscribeToServices");
			System.out.println("service: "+service);
			assertContainsField(service, "type");
			assertContainsField(service, "providerId");
			assertContainsField(service, "tags");
			assertContainsField(service, "scope");
			assertContainsField(service, "networkNames");
			assertContainsField(service, "unrestricted");
			
			dummy.killComponent().get();
		});
	}

	//-------- helper methods --------
	
	/**
	 *  Retrieve URL content and return as string (UTF-8).
	 */
	protected String getUrlContent(String urlstring)
	{
		// Blocking IO on external thread
		Future<String>	ret	= new Future<>();
		SSimulation.addBlocker(ret);
		new Thread(()->
		{
			try
			{
				URL	url	= new URL(urlstring);
				URLConnection	con	= url.openConnection();
				
				// On subscription with empty initial result -> request another result (long polling)
				if(con instanceof HttpURLConnection && ((HttpURLConnection)con).getResponseCode()==202
					&& con.getContentLength()==0)
				{
					String	callid	= con.getHeaderField("x-jadex-callid");
					con	= url.openConnection();
					con.setRequestProperty("x-jadex-callid", callid);
				}
				
				try(InputStream	is	= con.getInputStream())
	//			try(InputStream	is	= url.openStream())
				{
					try(Scanner	s	= new Scanner(is, "UTF-8"))
					{
						ret.setResult(s.hasNext() ? s.useDelimiter("\\A").next() : "");
					}
				}
			}
			catch(Exception e)
			{
				ret.setException(e);
			}
		}).start();
		
		//  Blocking get() with component suspendable on component thread
		return ret.get();
	}
	
	/**
	 *  Check that the JSON string contains a given field.
	 */
	protected void assertContainsField(String json, String field)
	{
		assertTrue("JSON misses field \""+field+"\": "+json, json.indexOf("\""+field+"\":")!=-1);
	}
}
