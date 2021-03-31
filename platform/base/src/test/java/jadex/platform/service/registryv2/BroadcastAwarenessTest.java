package jadex.platform.service.registryv2;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Collection;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.awareness.IAwarenessService;
import jadex.commons.SUtil;
import jadex.commons.security.SSecurity;

/**
 *  Test basic search functionality with broadcast awareness only
 *  (i.e. no super peer).
 */
public class BroadcastAwarenessTest	extends AbstractSearchQueryTest
{
	//-------- constants --------
	
	/** Client configuration for platform used for searching. */
	public static final IPlatformConfiguration	CLIENTCONF;

	/** Plain provider configuration. */
	public static final IPlatformConfiguration	PROCONF;

	static
	{
		// Fixed custom port for broadcast, try 10 times (windows ipv6 problem?)
		int	port	= -1;
		for(int i=0; i<10; i++)
		{
			port	= SSecurity.getSecureRandom().nextInt(Short.MAX_VALUE*2-1023)+1024;  // random value from 1024 to 2^16-1
			try(DatagramSocket	recvsocket = new DatagramSocket(null))
			{
				recvsocket.setReuseAddress(true);
				recvsocket.bind(new InetSocketAddress(port));
				break;
			}
			catch(IOException se)
			{
				System.out.println("port "+port+" problem?\n"+SUtil.getExceptionStacktrace(se));
			}
		}
		System.out.println("BroadcastAwarenessTest custom port: "+port);
		
		IPlatformConfiguration	baseconf	= STest.getDefaultTestConfig(BroadcastAwarenessTest.class);
		baseconf.setValue("superpeerclient.awaonly", true);
		baseconf.setValue("intravmawareness", false);
		baseconf.setValue("broadcastawareness", true);
		baseconf.setValue("broadcastawareness.port", port);
//		baseconf.setValue("superpeerclient.debugservices", "ITestService");
		baseconf.setDefaultTimeout(Starter.getScaledDefaultTimeout(null, WAITFACTOR*3));
		baseconf.getExtendedPlatformConfiguration().setDebugFutures(true);

		// Remote only -> no simulation please
		baseconf.getExtendedPlatformConfiguration().setSimul(false);
		baseconf.getExtendedPlatformConfiguration().setSimulation(false);
		
		CLIENTCONF	= baseconf.clone();
		CLIENTCONF.setPlatformName("client");
		
		PROCONF	= baseconf.clone();
		PROCONF.addComponent(GlobalProviderAgent.class);
		PROCONF.addComponent(NetworkProviderAgent.class);
		PROCONF.addComponent(LocalProviderAgent.class);
		PROCONF.setPlatformName("provider");
	}
	
	//-------- constructors --------
	
	/**
	 *  Create the test.
	 */
	public BroadcastAwarenessTest()
	{
		super(true, CLIENTCONF, PROCONF, null, null);
	}
	
	//-------- methods --------

	/**
	 *  Test bare awareness results.
	 */
	@Test
	public void	testBareAwareness()
	{
		IExternalAccess	client	= createPlatform(CLIENTCONF);		
		createPlatform(PROCONF);	
		createPlatform(PROCONF);
		
		IAwarenessService	pawa	= client.searchService(new ServiceQuery<>(IAwarenessService.class)).get();
		assertTrue("Found broadcast awareness? "+pawa, pawa.toString().toLowerCase().contains("broadcast"));
		
		Collection<IComponentIdentifier>	found	= pawa.searchPlatforms().get();
		assertEquals(found.toString(), 2, found.size());
	}
}
