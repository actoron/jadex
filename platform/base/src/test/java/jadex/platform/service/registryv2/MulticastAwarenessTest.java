package jadex.platform.service.registryv2;


import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;

/**
 *  Test basic search functionality with multicast awareness only
 *  (i.e. no superpeer).
 */
public class MulticastAwarenessTest	extends AbstractInfrastructureTest
{
	//-------- constants --------
	
	/** Client configuration for platform used for searching. */
	public static final IPlatformConfiguration	CLIENTCONF;

	/** Plain provider configuration. */
	public static final IPlatformConfiguration	PROCONF;

	static
	{
		IPlatformConfiguration	baseconf	= STest.getDefaultTestConfig();
		baseconf.setValue("superpeerclient.awaonly", true);
		baseconf.setValue("superpeerclient.pollingrate", WAITFACTOR/2); 	// -> 750 millis.
		baseconf.setValue("passiveawarenessintravm", false);
		baseconf.setValue("passiveawarenessmulticast", true);
		baseconf.setDefaultTimeout(Starter.getScaledDefaultTimeout(null, WAITFACTOR*2));

		// Remote only -> no simulation please
		baseconf.getExtendedPlatformConfiguration().setSimul(false);
		baseconf.getExtendedPlatformConfiguration().setSimulation(false);
		
		CLIENTCONF	= baseconf.clone();
		CLIENTCONF.setPlatformName("client_*");
		
		PROCONF	= baseconf.clone();
		PROCONF.addComponent(ProviderAgent.class);
		PROCONF.addComponent(LocalProviderAgent.class);
		PROCONF.setPlatformName("provider_*");
	}
	
	//-------- test cases --------
	
	/**
	 *  cases for testing: services
	 */
	@Test
	public void	testServices()
	{
		// 1) search for service -> not found (test if works with no superpeers and no other platforms)
		System.out.println("1) search for service");
		IExternalAccess	client	= createPlatform(CLIENTCONF);
		waitALittle(client);
		Collection<ITestService>	result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
		Assert.assertTrue(""+result, result.isEmpty());
		
		// 2) start remote platform, search for service -> test if awa fallback works with one platform 
		System.out.println("2) start remote platform, search for service");
		IExternalAccess	pro1	= createPlatform(PROCONF);
		result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
		Assert.assertEquals(""+result, 1, result.size());
		
		// 3) start remote platform, search for service -> test if awa fallback works with two platforms 
		System.out.println("3) start remote platform, search for service");
		/*IExternalAccess	pro2	=*/ createPlatform(PROCONF);
		result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
		Assert.assertEquals(""+result, 2, result.size());
		
		// 4) kill one remote platform, search for service -> test if remote disconnection and service removal works
		System.out.println("4) kill one remote platform, search for service");
		removePlatform(pro1);
		result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
		Assert.assertEquals(""+result, 1, result.size());
	}
}
