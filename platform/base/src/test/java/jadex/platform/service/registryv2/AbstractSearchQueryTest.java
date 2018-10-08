package jadex.platform.service.registryv2;


import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Test basic search and query managing functionality with a client and some providers.
 *  Abstract base implementation to be overriden for testing different infrastructure scenarios.
 */
public abstract class AbstractSearchQueryTest	extends AbstractInfrastructureTest
{
	//-------- attributes --------
	
	/** Do client and provider see each other via awareness? */
	protected boolean	awa;
	
	/** Client configuration for platform used for searching. */
	protected IPlatformConfiguration	clientconf;

	/** Plain provider configuration. */
	protected IPlatformConfiguration	proconf;

	/** Local super peer platform configuration (if any). */
	protected IPlatformConfiguration	spconf;

	/** Global super peer platform configuration (if any). */
	protected IPlatformConfiguration	sspconf;
	
	//-------- constructors --------
	
	/**
	 *  Create a search and query test for the given settings.
	 *  @param awa Do client and provider see each other via awareness?
	 */
	public AbstractSearchQueryTest(boolean awa, IPlatformConfiguration clientconf, IPlatformConfiguration proconf, IPlatformConfiguration spconf, IPlatformConfiguration sspconf)
	{
		this.awa	= awa;
		this.clientconf	= clientconf;
		this.proconf	= proconf;
		this.spconf	= spconf;
		this.sspconf	= sspconf;
	}

	//-------- test cases --------
	
	/**
	 *  cases for testing: queries
	 */
	@Test
	public void	testQueries()
	{
		// SSP is optional (TODO: support ssp started after client)
		if(sspconf!=null)
		{
			createPlatform(sspconf);
		}
		
		// 1) start client platform and add query -> not found (test if works with no superpeers and no other platforms)
		System.out.println("1) start client platform and add query");
		IExternalAccess	client	= createPlatform(clientconf);
		ISubscriptionIntermediateFuture<ITestService>	results	= client.addQuery(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL));
		waitALittle(client);
		Assert.assertEquals(Collections.emptySet(), new LinkedHashSet<>(results.getIntermediateResults()));
		
		//-------- Tests with awareness fallback only (no SP) --------
		
		IExternalAccess	pro1, pro2;
		if(awa)
		{
			// 2) start provider platform, wait for service -> test if awa fallback works with one platform, also checks local duplicate removal over time
			System.out.println("2) start provider platform, wait for service");
			pro1	= createPlatform(proconf);
			ITestService	svc	= results.getNextIntermediateResult();
			Assert.assertEquals(""+svc, pro1.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
			
			// 3) start provider platform, wait for service -> test if awa fallback works with two platforms 
			System.out.println("3) start provider platform, wait for service");
			pro2	= createPlatform(proconf);
			svc	= results.getNextIntermediateResult();
			Assert.assertEquals(""+svc, pro2.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
		}
		else
		{
			// -> test if platforms don't see each other without SP.
			System.out.println("2/3) start provider platforms, wait for services");
			pro1	= createPlatform(proconf);
			pro2	= createPlatform(proconf);
			waitALittle(client);
			Assert.assertEquals(Collections.emptySet(), new LinkedHashSet<>(results.getIntermediateResults()));
		}

		//-------- Tests with SP if any --------
		
		if(spconf!=null)
		{
			// 4) start SP, wait for connection from provider platforms and client platform 
			System.out.println("4) start SP, wait for connection from provider platforms and client platform");
			IExternalAccess	sp	= createPlatform(spconf);
			waitForSuperpeerConnections(sp, client, pro1, pro2);
			waitALittle(client);
			if(awa)
			{
				// -> should get no service; test if duplicate removal works with SP
				Assert.assertEquals(Collections.emptySet(), new LinkedHashSet<>(results.getIntermediateResults()));
			}
			else
			{
				// -> should now receive the two services from query.
				Set<IComponentIdentifier>	providers1	= new LinkedHashSet<>();
				ITestService	svc	= results.getNextIntermediateResult();
				providers1.add(((IService)svc).getServiceId().getProviderId().getRoot());
				svc	= results.getNextIntermediateResult();
				providers1.add(((IService)svc).getServiceId().getProviderId().getRoot());
				Set<IComponentIdentifier>	providers2	= new LinkedHashSet<>();
				providers2.add(pro1.getId());
				providers2.add(pro2.getId());
				Assert.assertEquals(Collections.emptySet(), new LinkedHashSet<>(results.getIntermediateResults()));
				Assert.assertEquals(providers1, providers2);
			}
			
			// 5) add second query -> wait for two services (test if works when already SP)
			System.out.println("5) add second query");
			ISubscriptionIntermediateFuture<ITestService>	results2	= client.addQuery(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL));
			Set<IComponentIdentifier>	providers1	= new LinkedHashSet<>();
			ITestService	svc	= results2.getNextIntermediateResult();
			providers1.add(((IService)svc).getServiceId().getProviderId().getRoot());
			svc	= results2.getNextIntermediateResult();
			providers1.add(((IService)svc).getServiceId().getProviderId().getRoot());
			Set<IComponentIdentifier>	providers2	= new LinkedHashSet<>();
			providers2.add(pro1.getId());
			providers2.add(pro2.getId());
			waitALittle(client);
			Assert.assertEquals(Collections.emptySet(), new LinkedHashSet<>(results2.getIntermediateResults()));
			Assert.assertEquals(providers1, providers2);
			
			// 6) start provider platform, wait for service in both queries -> test if works for existing queries (before and after SP)
			System.out.println("6) start remote platform, wait for service in both queries");
			IExternalAccess	pro3	= createPlatform(proconf);
			svc	= results.getNextIntermediateResult();
			Assert.assertEquals(""+svc, pro3.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
			svc	= results2.getNextIntermediateResult();
			Assert.assertEquals(""+svc, pro3.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
	
			// 7) kill SP, start provider platform, wait for service on both queries
			System.out.println("7) kill SP, start remote platform, wait for service on both queries");
			removePlatform(sp);
			IExternalAccess	pro4	= createPlatform(proconf);
			if(awa)
			{
				// -> test if re-fallback to awa works for queries
				svc	= results.getNextIntermediateResult();
				Assert.assertEquals(""+svc, pro4.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
				svc	= results2.getNextIntermediateResult();
				Assert.assertEquals(""+svc, pro4.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
			}
			else
			{
				// -> test if disconnection from SP works (new services not found)
				waitALittle(client);
				Assert.assertEquals(Collections.emptySet(), new LinkedHashSet<>(results.getIntermediateResults()));
				Assert.assertEquals(Collections.emptySet(), new LinkedHashSet<>(results2.getIntermediateResults()));
			}
		}
	}
	
	/**
	 *  cases for testing: services
	 */
	@Test
	public void	testServices()
	{
		// SSP is optional (TODO: support ssp started after client)
		if(sspconf!=null)
		{
			createPlatform(sspconf);
		}
		
		//-------- Tests with awareness fallback only (no SP) --------
		
		// 1) start client platform and search for service -> not found (test if works with no super peers and no other platforms)
		System.out.println("1) start client platform and search for service");
		IExternalAccess	client	= createPlatform(clientconf);
		waitALittle(client);
		Collection<ITestService>	result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
		Assert.assertTrue(""+result, result.isEmpty());
		
		IExternalAccess	pro1, pro2;
		if(awa)
		{
			// 2) start provider platform, search for service -> test if awa fallback works with one platform 
			System.out.println("2) start provider platform, search for service");
			pro1	= createPlatform(proconf);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
			Assert.assertEquals(""+result, 1, result.size());
			
			// 3) start provider platform, search for service -> test if awa fallback works with two platforms 
			System.out.println("3) start provider platform, search for service");
			pro2	= createPlatform(proconf);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
			Assert.assertEquals(""+result, 2, result.size());
			
			// 4) kill one provider platform, search for service -> test if platform is removed from awareness
			System.out.println("4) kill one provider platform, search for service");
			removePlatform(pro1);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
			Assert.assertEquals(""+result, 1, result.size());
		}
		else
		{
			// -> test if platforms don't see each other without SP.
			System.out.println("2/3/4) start provider platforms, wait for services");
			pro1	= createPlatform(proconf);
			pro2	= createPlatform(proconf);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
			Assert.assertEquals(0, result.size());
			removePlatform(pro1);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
			Assert.assertEquals(0, result.size());
		}

		//-------- Tests with SP if any --------
		
		if(spconf!=null)
		{
			// 5) start SP, wait for connection from provider platforms and client platform, search for service -> test if SP connection works
			System.out.println("5) start SP, wait for connection from provider platforms and client platform, search for service");
			IExternalAccess	sp	= createPlatform(spconf);
			waitForSuperpeerConnections(sp, client, pro2);
			waitALittle(client);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
			Assert.assertEquals(""+result, 1, result.size());
			
			// 6) start provider platform, wait for connection, search for service -> test if search works for new platform and existing SP
			System.out.println("6) start provider platform, search for service");
			pro1	= createPlatform(proconf);
			waitForSuperpeerConnections(sp, pro1);
			waitALittle(client);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
			Assert.assertEquals(""+result, 2, result.size());
			
			// 7) kill one provider platform, search for service -> test if remote disconnection and service removal works
			System.out.println("7) kill one provider platform, search for service");
			removePlatform(pro1);
			waitALittle(client);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
			Assert.assertEquals(""+result, 1, result.size());
	
			// 8) kill SP, search for service -> test if re-fallback to awa works
			System.out.println("8) kill SP, search for service");
			removePlatform(sp);
			waitALittle(client);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get();
			Assert.assertEquals(""+result, awa?1:0, result.size());
		}
	}
}
