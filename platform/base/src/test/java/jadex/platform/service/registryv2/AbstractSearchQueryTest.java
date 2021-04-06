package jadex.platform.service.registryv2;


import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.platform.service.registry.SuperpeerClientAgent;

/**
 *  Test basic search and query managing functionality with a client and some providers.
 *  Abstract base implementation to be overridden for testing different infrastructure scenarios.
 */
public abstract class AbstractSearchQueryTest	extends AbstractInfrastructureTest
{
	//-------- attributes --------
	
	/** Do client and provider see each other via awareness? */
	protected boolean awa;
	
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
		ISubscriptionIntermediateFuture<ITestService>	results	= client.addQuery(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL));
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
			ITestService	svc2	= results.getNextIntermediateResult();
			Assert.assertEquals(""+svc, pro1.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
			Assert.assertEquals(""+svc2, pro1.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
			
			// 3) start provider platform, wait for service -> test if awa fallback works with two platforms 
			System.out.println("3) start provider platform, wait for service");
			pro2	= createPlatform(proconf);
			svc	= results.getNextIntermediateResult();
			svc2 = results.getNextIntermediateResult();
			Assert.assertEquals(""+svc, pro2.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
			Assert.assertEquals(""+svc2, pro2.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
		}
		else
		{
			// without SP -> test that platform finds no / only global services.
			System.out.println("2/3) start provider platforms, wait for services");
			pro1	= createPlatform(proconf);
			pro2	= createPlatform(proconf);
			waitForRegistryWithProvider(client, pro1, true);
			waitForRegistryWithProvider(client, pro2, true);
			Collection<ITestService> col = results.getIntermediateResults();
			Assert.assertEquals(col.toString(), sspconf==null ? 0 : 2, col.size());
		}

		//-------- Tests with SP if any --------
		
		if(spconf!=null)
		{
			// 4) start SP, wait for connection from provider platforms and client platform 
			System.out.println("4) start SP, wait for connection from provider platforms and client platform");
			IExternalAccess	sp	= createPlatform(spconf);
			waitForSuperpeerConnections(sp, client, pro1, pro2);
			waitForRegistryClient(client, false);
			if(awa)
			{
				// -> should get no service; test if duplicate removal works with SP
				Assert.assertEquals(Collections.emptySet(), new LinkedHashSet<>(results.getIntermediateResults()));
			}
			else
			{
				// -> should now receive the four services from query.
				Set<IComponentIdentifier> providers1 = new LinkedHashSet<>();
				ITestService svc = results.getNextIntermediateResult();
				providers1.add(((IService)svc).getServiceId().getProviderId().getRoot());
				svc	= results.getNextIntermediateResult();
				providers1.add(((IService)svc).getServiceId().getProviderId().getRoot());
				svc	= results.getNextIntermediateResult();
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
			ISubscriptionIntermediateFuture<ITestService>	results2	= client.addQuery(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL));
			
			Set<IComponentIdentifier> providers1 = new LinkedHashSet<>();
			ITestService svc = results2.getNextIntermediateResult();
			providers1.add(((IService)svc).getServiceId().getProviderId().getRoot());
			svc	= results2.getNextIntermediateResult();
			providers1.add(((IService)svc).getServiceId().getProviderId().getRoot());
			
			Set<IComponentIdentifier> providers2 = new LinkedHashSet<>();
			providers2.add(pro1.getId());
			providers2.add(pro2.getId());
			
			if(sspconf!=null || SuperpeerClientAgent.SPCACHE)	// wait for the two global services also
			{
				svc	= results2.getNextIntermediateResult();
				providers1.add(((IService)svc).getServiceId().getProviderId().getRoot());
				svc	= results2.getNextIntermediateResult();
				providers1.add(((IService)svc).getServiceId().getProviderId().getRoot());
				providers2.add(pro1.getId());
				providers2.add(pro2.getId());
			}
			
			Assert.assertEquals(Collections.emptySet(), new LinkedHashSet<>(results2.getIntermediateResults()));
			Assert.assertEquals(providers1, providers2);
			
			// 6) start provider platform, wait for service in both queries -> test if works for existing queries (before and after SP)
			System.out.println("6) start remote platform, wait for service in both queries");
			IExternalAccess	pro3	= createPlatform(proconf);
			svc	= results.getNextIntermediateResult();
			Assert.assertEquals(""+svc, pro3.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
			svc	= results2.getNextIntermediateResult();
			Assert.assertEquals(""+svc, pro3.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
			
			if(sspconf!=null || SuperpeerClientAgent.SPCACHE)	// wait for the two global services also
			{
				svc	= results.getNextIntermediateResult();
				Assert.assertEquals(""+svc, pro3.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
				svc	= results2.getNextIntermediateResult();
				Assert.assertEquals(""+svc, pro3.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
			}
	
			// 7) kill SP, start provider platform, wait for service on both queries
			System.out.println("7) kill SP, start remote platform, wait for service on both queries");
			removePlatform(sp);
			if(awa && sspconf==null && !SuperpeerClientAgent.SPCACHE)
			{
				// After fallback to awa -> global services are now found (hack???)
				svc	= results.getNextIntermediateResult();
//				svc	= results.getNextIntermediateResult();
				svc	= results2.getNextIntermediateResult();
				svc	= results2.getNextIntermediateResult();
				svc	= results2.getNextIntermediateResult();
			}
			
			IExternalAccess	pro4 = createPlatform(proconf);
			if(awa)
			{
				// -> test if re-fallback to awa works for queries
				waitForRegistryClient(client, true);
				svc	= results.getNextIntermediateResult();
				Assert.assertEquals(""+svc, pro4.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
				svc	= results.getNextIntermediateResult();
				Assert.assertEquals(""+svc, pro4.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
				svc	= results2.getNextIntermediateResult();
				Assert.assertEquals(""+svc, pro4.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
				svc	= results2.getNextIntermediateResult();
				Assert.assertEquals(""+svc, pro4.getId(), ((IService)svc).getServiceId().getProviderId().getRoot());
			}
			else
			{
				// -> test if disconnection from SP works (new services not found)
				waitForRegistryWithProvider(client, pro4, true);
				Collection<ITestService> col1 = results.getIntermediateResults();
				Collection<ITestService> col2 = results2.getIntermediateResults();
				Assert.assertEquals(1, new LinkedHashSet<>(col1).size());
				Assert.assertEquals(1, new LinkedHashSet<>(col2).size());
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
		System.err.println("1a) start client platform and search for service");
		IExternalAccess	client	= createPlatform(clientconf);
		System.err.println("1b) start client platform and search for service");
		waitForRegistryClient(client, true);
		System.err.println("1c) start client platform and search for service");
		Collection<ITestService>	result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
		System.err.println("1d) start client platform and search for service");
		Assert.assertTrue(""+result, result.isEmpty());
		
		IExternalAccess	pro1, pro2;
		if(awa)
		{
			// 2) start provider platform, search for service -> test if awa fallback works with one platform 
			System.out.println("2) start provider platform, search for service");
			System.err.println("2a) start provider platform, search for service");
			pro1	= createPlatform(proconf);
			System.err.println("2a) start provider platform, search for service");
			waitForRegistryWithProvider(client, pro1, true);
			System.err.println("2a) start provider platform, search for service");
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
			System.err.println("2a) start provider platform, search for service");
			Assert.assertEquals(""+result, 2, result.size()); // global + network provider
			
			// 3) start provider platform, search for service -> test if awa fallback works with two platforms 
			System.out.println("3) start provider platform, search for service");
			pro2	= createPlatform(proconf);
			waitForRegistryWithProvider(client, pro2, true);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
			Assert.assertEquals(""+result, 4, result.size());
			
			// 3b) search without scope (must deliver scope and network services
			for(ITestService ser: result)
			{
				ITestService ts = null;
				try
				{
					ts	= client.searchService(new ServiceQuery<>(ITestService.class).setProvider(((IService)ser).getServiceId().getProviderId())).get();
				}
				catch(Exception e)
				{
					System.out.println("exception: "+e);
				}
				Assert.assertNotEquals(ts, null);
			}
			
			// 4) kill one provider platform, search for service -> test if platform is removed from awareness
			System.out.println("4) kill one provider platform, search for service");
			removePlatform(pro1);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
			Assert.assertEquals(""+result, 2, result.size());
		}
		else
		{
			// -> test if platforms don't see each other without SP.
			System.out.println("2/3/4) start provider platforms, wait for services");
			pro1	= createPlatform(proconf);
			pro2	= createPlatform(proconf);
			waitForRegistryWithProvider(client, pro1, true);
			waitForRegistryWithProvider(client, pro2, true);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
			System.out.println("found: "+result.size());
			Assert.assertEquals(2, result.size());
			removePlatform(pro1);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
			Assert.assertEquals(1, result.size());
		}

		//-------- Tests with SP if any --------
		
		if(spconf!=null)
		{
			// 5) start SP, wait for connection from provider platforms and client platform, search for service -> test if SP connection works
			System.out.println("5) start SP, wait for connection from provider platforms and client platform, search for service");
			IExternalAccess	sp	= createPlatform(spconf);
			waitForSuperpeerConnections(sp, client, pro2);
			waitForRegistryWithProvider(client, pro2, false);
//			waitALittle(client);	// Hack for timeout in CI Pipeline!?
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
			Assert.assertEquals(client.toString()+": "+result, sspconf==null && !SuperpeerClientAgent.SPCACHE ? 1:2, result.size());
			
			// 6) start provider platform, wait for connection, search for service -> test if search works for new platform and existing SP
			System.out.println("6) start provider platform, search for service");
			pro1	= createPlatform(proconf);
			waitForSuperpeerConnections(sp, pro1);
			waitForRegistryClient(client, false);
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
			Assert.assertEquals("found: "+result+", new platform: "+pro1.getId(), sspconf==null && !SuperpeerClientAgent.SPCACHE ? 2 : 4, result.size());
			
			// 6b) search without scope (must deliver scope and network services)
			for(ITestService ser: result)
			{
				ITestService ts = null;
				try
				{
					ts	= client.searchService(new ServiceQuery<>(ITestService.class).setProvider(((IService)ser).getServiceId().getProviderId())).get();
				}
				catch(Exception e)
				{
					System.out.println("exception: "+e);
				}
				Assert.assertNotEquals(ts, null);
			}
			
			// 7) kill one provider platform, search for service -> test if remote disconnection and service removal works
			System.out.println("7) kill provider platform "+pro1.getId()+", search for service");
			removePlatform(pro1);
			waitForRegistryClient(client, false);
			int	num	= sspconf==null && !SuperpeerClientAgent.SPCACHE ? 1 : 2;	// expected number of remaining services
			// retry at most 10 times until old services expunged from registry
			// hack!!! should only be 2*WAITFACTOR but leads to heisenbugs?
			for(int i=0; i<=10; i++)
			{
				if(i>0) waitALittle(client);
				result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
				if(result.size()<=num) break;
				System.out.println("7"+(char)('a'+i)+") results: "+result.size()+", "+result);
			}
			Assert.assertEquals(""+result, num, result.size());
	
			// 8) kill SP, search for service -> test if re-fallback to awa works
			System.out.println("8) kill SP, search for service");
			removePlatform(sp);
			waitALittle(client);
			waitALittle(client);	// two waits for disconnection, because contimeout = 2* WAITFACTOR
			result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
			Assert.assertEquals(""+result, awa? 2: 1, result.size());
		}
	}
	
	/**
	 *  Wait to allow remote platform/registry interaction.
	 *  The idea is that the registry is roughly FCFS so
	 *  when we start another service and that service appears in the registry
	 *  all previous activity should also be completed.
	 */
	protected void waitForRegistryClient(IExternalAccess client, boolean global)
	{
		Logger.getLogger(getClass().getName()).info("waitForRegistryClient0: "+client+", "+true);
		// Can only use global when ssp available. Otherwise uses awa fallback via network.
		global	= global && sspconf!=null;
		
		IExternalAccess	marker	= Starter.createPlatform(clientconf).get();
		ISubscriptionIntermediateFuture<IMarkerService>	sub	= client.addQuery(new ServiceQuery<>(IMarkerService.class, global ? ServiceScope.GLOBAL : ServiceScope.NETWORK));
		IExternalAccess	agent	= marker.addComponent(global ? new GlobalMarkerAgent() : new NetworkMarkerAgent()).get();
		IComponentIdentifier	found;
		do
		{
			// TODO: use listener instead of blocking API to exclude API as heisenbug cause
			found	= ((IService)sub.getNextIntermediateResult()).getServiceId().getProviderId();
			Logger.getLogger(getClass().getName()).info("Found marker: "+found+"; expecting: "+agent.getId()+", "+agent.getId().equals(found));
		}
		while(!agent.getId().equals(found));
			
		marker.killComponent().get();
		marker	= null;
	}
	
	/**
	 *  Wait to allow remote platform/registry interaction.
	 *  The idea is that the registry is roughly FCFS so
	 *  when we start another service and that service appears in the registry
	 *  all previous activity should also be completed.
	 */
	protected void waitForRegistryWithProvider(IExternalAccess client, IExternalAccess provider, boolean global)
	{
		// Can only use global when ssp available. Otherwise uses awa fallback via network.
		global	= global && sspconf!=null;
		
		ISubscriptionIntermediateFuture<IMarkerService>	sub	= client.addQuery(new ServiceQuery<>(IMarkerService.class, global ? ServiceScope.GLOBAL : ServiceScope.NETWORK));
		IExternalAccess	agent	= provider.addComponent(global ? new GlobalMarkerAgent() : new NetworkMarkerAgent()).get();
		while(!agent.getId().equals(((IService)sub.getNextIntermediateResult()).getServiceId().getProviderId()))
		{
		}
		agent.killComponent().get();
	}
	
	@Service
	public static interface IMarkerService {}
	
	@Agent
	@ProvidedServices(@ProvidedService(type=IMarkerService.class, scope=ServiceScope.GLOBAL))
	public static class GlobalMarkerAgent	implements IMarkerService {}
	
	@Agent
	@ProvidedServices(@ProvidedService(type=IMarkerService.class, scope=ServiceScope.NETWORK))
	public static class NetworkMarkerAgent	implements IMarkerService {}
}
