package jadex.platform.service.registryv2;


import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.test.util.STest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Test basic search and query managing functionality
 *  with and without one single superpeer.
 */
public class SuperpeerClientTest	extends AbstractInfrastructureTest
{
	//-------- constants --------
	
	/** Client configuration for platform used for searching. */
	public static final IPlatformConfiguration	CLIENTCONF;

	/** Plain provider configuration. */
	public static final IPlatformConfiguration	PROCONF;

	/** Superpeer platform configuration. */
	public static final IPlatformConfiguration	SPCONF;

	static
	{
		IPlatformConfiguration	baseconf	= STest.getDefaultTestConfig();
		baseconf.setValue("superpeerclient.awaonly", false);
		baseconf.setValue("superpeerclient.pollingrate", WAITFACTOR/2); 	// -> 750 millis.
		
		CLIENTCONF	= baseconf.clone();
		CLIENTCONF.setPlatformName("client_*");
		
		PROCONF	= baseconf.clone();
		PROCONF.addComponent(ProviderAgent.class);
		PROCONF.addComponent(LocalProviderAgent.class);
		PROCONF.setPlatformName("provider_*");
		
		SPCONF	= baseconf.clone();
		SPCONF.addComponent(SuperpeerRegistryAgent.class);
		SPCONF.setPlatformName("SP_*");
	}
	
	//-------- test cases --------
	
	/**
	 *  cases for testing: queries
	 */
	@Test
	public void	testQueries()
	{
		// 1) add query -> not found (test if works with no superpeers and no other platforms)
		System.out.println("1) add query");
		IExternalAccess	client	= createPlatform(CLIENTCONF);
		ISubscriptionIntermediateFuture<ITestService>	results	= client.addQuery(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL));
		doWait(client);
		Assert.assertEquals("1) ", Collections.emptySet(), new LinkedHashSet<>(results.getIntermediateResults()));
		
		// 2) start remote platform, wait for service -> test if awa fallback works with one platform, also checks local duplicate removal over time
		System.out.println("2) start remote platform, wait for service");
		IExternalAccess	pro1	= createPlatform(PROCONF);
		ITestService	svc	= results.getNextIntermediateResult();
		Assert.assertEquals("2) "+svc, pro1.getId(), ((IService)svc).getId().getProviderId().getRoot());
		
		// 3) start remote platform, wait for service -> test if awa fallback works with two platforms 
		System.out.println("3) start remote platform, wait for service");
		IExternalAccess	pro2	= createPlatform(PROCONF);
		svc	= results.getNextIntermediateResult();
		Assert.assertEquals("3) "+svc, pro2.getId(), ((IService)svc).getId().getProviderId().getRoot());
		
		// 4) start SP, wait for connection from remote platforms and local platform -> should get no service; test if duplicate removal works with SP
		System.out.println("4) start SP, wait for connection from remote platforms and local platform");
		IExternalAccess	sp	= createPlatform(SPCONF);
		waitForSuperpeerConnections(sp, client, pro1, pro2);
		doWait(client);
		Assert.assertEquals("4) ", Collections.emptySet(), new LinkedHashSet<>(results.getIntermediateResults()));
		
		// 5) add second query -> wait for two services (test if works when already SP)
		System.out.println("5) add second query");
		ISubscriptionIntermediateFuture<ITestService>	results2	= client.addQuery(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL));
		Set<IComponentIdentifier>	providers1	= new LinkedHashSet<>();
		svc	= results2.getNextIntermediateResult();
		providers1.add(((IService)svc).getId().getProviderId().getRoot());
		svc	= results2.getNextIntermediateResult();
		providers1.add(((IService)svc).getId().getProviderId().getRoot());
		Set<IComponentIdentifier>	providers2	= new LinkedHashSet<>();
		providers2.add(pro1.getId());
		providers2.add(pro2.getId());
		doWait(client);
		Assert.assertEquals("5) ", Collections.emptySet(), new LinkedHashSet<>(results2.getIntermediateResults()));
		Assert.assertEquals(providers1, providers2);
		
		// 6) start remote platform, wait for service in both queries -> test if works for existing queries (before and after SP)
		System.out.println("6) start remote platform, wait for service in both queries");
		IExternalAccess	pro3	= createPlatform(PROCONF);
		svc	= results.getNextIntermediateResult();
		Assert.assertEquals(""+svc, pro3.getId(), ((IService)svc).getId().getProviderId().getRoot());
		svc	= results2.getNextIntermediateResult();
		Assert.assertEquals(""+svc, pro3.getId(), ((IService)svc).getId().getProviderId().getRoot());

		// TODO
//		// 7) kill SP, start remote platform, wait for service on both queries -> test if re-fallback to awa works for queries
//		System.out.println("7) kill SP, start remote platform, wait for service on both queries");
//		removePlatform(sp);
//		IExternalAccess	pro4	= createPlatform(PROCONF);
//		svc	= results.getNextIntermediateResult();
//		Assert.assertEquals(""+svc, pro4.getId(), ((IService)svc).getId().getProviderId().getRoot());
//		svc	= results2.getNextIntermediateResult();
//		Assert.assertEquals(""+svc, pro4.getId(), ((IService)svc).getId().getProviderId().getRoot());
	}
	
	/**
	 *  cases for testing: services
	 */
	@Test
	public void	testServices()
	{
		// 1) search for service -> not found (test if works with no superpeers and no other platforms)
		System.out.println("1) search for service");
		IExternalAccess	client	= createPlatform(CLIENTCONF);
		doWait(client);
		Collection<ITestService>	result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
		Assert.assertTrue(""+result, result.isEmpty());
		
		// 2) start remote platform, search for service -> test if awa fallback works with one platform 
		System.out.println("2) start remote platform, search for service");
		IExternalAccess	pro1	= createPlatform(PROCONF);
		result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
		Assert.assertEquals(""+result, 1, result.size());
		
		// 3) start remote platform, search for service -> test if awa fallback works with two platforms 
		System.out.println("3) start remote platform, search for service");
		IExternalAccess	pro2	= createPlatform(PROCONF);
		result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
		Assert.assertEquals(""+result, 2, result.size());
		
		// 4) start SP, wait for connection from remote platforms and local platform, search for service -> test if SP connection works
		System.out.println("4) start SP, wait for connection from remote platforms and local platform, search for service");
		IExternalAccess	sp	= createPlatform(SPCONF);
		waitForSuperpeerConnections(sp, client, pro1, pro2);
		doWait(client);
		result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
		Assert.assertEquals(""+result, 2, result.size());
		
		// 5) kill one remote platform, search for service -> test if remote disconnection and service removal works
		System.out.println("5) kill one remote platform, search for service");
		removePlatform(pro1);
		result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
		Assert.assertEquals(""+result, 1, result.size());

		// TODO
//		// 6) kill SP, search for service -> test if re-fallback to awa works
//		System.out.println("6) kill SP, search for service");
//		removePlatform(sp);
//		result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
//		Assert.assertEquals(""+result, 1, result.size());
	}
}
