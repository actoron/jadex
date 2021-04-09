package jadex.platform.service.registryv2;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.awareness.IAwarenessService;
import jadex.commons.SUtil;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.security.SSecurity;

/**
 *  Test basic search functionality with multicast awareness only
 *  (i.e. no super peer).
 */
public class MulticastAwarenessTest	extends AbstractSearchQueryTest
{
	//-------- constants --------
	
	/** Client configuration for platform used for searching. */
	public static final IPlatformConfiguration	CLIENTCONF;

	/** Plain provider configuration. */
	public static final IPlatformConfiguration	PROCONF;
	
	static
	{
		// Fixed custom port for multicast, try 10 times (windows ipv6 problem?)
		// https://stackoverflow.com/questions/3947555/java-net-socketexception-unrecognized-windows-sockets-error-0-jvm-bind-jboss
		int	port	= -1;
		for(int i=0; i<10; i++)
		{
			port	= SSecurity.getSecureRandom().nextInt(Short.MAX_VALUE*2-1023)+1024;  // random value from 1024 to 2^16-1
			try
			{
				@SuppressWarnings({ "unused", "resource" })	// Hack!!! do not close socket to keep port available for test
				MulticastSocket	recvsocket = new MulticastSocket(port);
				break;
			}
			catch(IOException se)
			{
				System.out.println("port "+port+" problem?\n"+SUtil.getExceptionStacktrace(se));
			}
		}
		System.out.println("MulticastAwarenessTest custom port: "+port);

		IPlatformConfiguration	baseconf	= STest.getRealtimeTestConfig(MulticastAwarenessTest.class);
		baseconf.setValue("superpeerclient.awaonly", true);
		baseconf.setValue("intravmawareness", false);
		baseconf.setValue("multicastawareness", true);
		baseconf.setValue("multicastawareness.port", port);
//		baseconf.setValue("debugservices", "IMarkerService");
		baseconf.setDefaultTimeout(Starter.getScaledDefaultTimeout(null, WAITFACTOR*3));
		baseconf.getExtendedPlatformConfiguration().setDebugFutures(true);
		
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
	public MulticastAwarenessTest()
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
		// Start client and fetch awa service.
		IExternalAccess	client	= createPlatform(CLIENTCONF);		
		IAwarenessService	pawa	= client.searchService(new ServiceQuery<>(IAwarenessService.class)).get();
		assertTrue("Found multicast awareness? "+pawa, pawa.toString().toLowerCase().contains("multicast"));
		
		// Start providers and check that they can be found
		IExternalAccess	pro1	= createPlatform(PROCONF);	
		IExternalAccess	pro2	= createPlatform(PROCONF);
		Set<IComponentIdentifier>	platforms	= new LinkedHashSet<IComponentIdentifier>(Arrays.asList(pro1.getId(), pro2.getId()));
		IIntermediateFuture<IComponentIdentifier>	results	= pawa.searchPlatforms();
		IComponentIdentifier	result	= results.getNextIntermediateResult();
		assertTrue("Found provider platform? "+platforms+", "+result, platforms.remove(result));
		result	= results.getNextIntermediateResult();
		assertTrue("Found provider platform? "+platforms+", "+result, platforms.remove(result));
	}

	//-------- heisenbug test --------
	
	public static void main(String[] args)
	{
		MulticastAwarenessTest	test	= new MulticastAwarenessTest();
		test.setup();
		while(true)
		{
			System.out.print(".");
			IExternalAccess	client	= test.createPlatform(test.clientconf);
			test.waitForRegistryClient(client, true);
			client.killComponent().get();
		}
	}
}
