package jadex.platform.service.registryv2;


import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.platform.service.registry.SuperpeerClientAgent;
import jadex.platform.service.registry.SuperpeerRegistryAgent;

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
		if(clientconf==null)
		{
			throw new IllegalArgumentException("Clientconf is required.");
		}
		else if(proconf==null)
		{
			throw new IllegalArgumentException("Proconf is required.");
		}
		else if(!awa && sspconf==null)
		{
			throw new IllegalArgumentException("Either awa or ssp required for platform discovery!");
		}
		
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
//		if(sspconf!=null)
//		{
//			createPlatform(sspconf);
//		}
		STest.runSimLocked(sspconf!=null ? sspconf : clientconf, ia ->
		{
			// 1) (maybe) start client platform and add query
			System.out.println("1) start client platform and add query");
			IExternalAccess	client	= null;
			if(sspconf!=null)
			{
				client	= createPlatform(clientconf);				
			}
			else
			{
				client	= ia.getExternalAccess();
			}
			ISubscriptionIntermediateFuture<ITestService>	results	= client.addQuery(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL));
			
			// Skip empty test to avoid unconditional wait -> wrong results here  will also be detected later
			// -> not found (test if works with no superpeers and no other platforms)
	//		waitALittle(client);
	//		Assert.assertEquals(Collections.emptySet(), new LinkedHashSet<>(results.getIntermediateResults()));
			
			// platform id -> sids
			Map<IComponentIdentifier, Set<IServiceIdentifier>>	resultmap	= new LinkedHashMap<>();
			
			//-------- Tests with awareness fallback only (no SP) --------
			
			IExternalAccess	pro1, pro2;
			if(awa)
			{
				// 2) start provider platform, wait for service -> test if awa fallback works with one platform, also checks local duplicate removal over time
				System.out.println("2) start provider platform, wait for service");
				pro1	= createPlatform(proconf);
				checkNextResultAndAdd(client, resultmap, results, pro1.getId());
				checkNextResultAndAdd(client, resultmap, results, pro1.getId());
				
				// 3) start provider platform, wait for service -> test if awa fallback works with two platforms 
				System.out.println("3) start provider platform, wait for service");
				pro2	= createPlatform(proconf);
				checkNextResultAndAdd(client, resultmap, results, pro2.getId());
				checkNextResultAndAdd(client, resultmap, results, pro2.getId());
			}
			else
			{
				// without SP -> test that platform finds only global services.
				System.out.println("2) start provider platform, wait for service");
				pro1	= createPlatform(proconf);
				if(sspconf!=null)
				{
					checkNextResultAndAdd(client, resultmap, results, pro1.getId());
				}
				
				// without SP -> test that platform finds only global services.
				System.out.println("3) start provider platform, wait for service");
				pro2	= createPlatform(proconf);
				if(sspconf!=null)
				{
					checkNextResultAndAdd(client, resultmap, results, pro2.getId());
				}
			}
	
			//-------- Tests with SP if any --------
			
			if(spconf!=null)
			{
				// 4) start SP, wait for connection from provider platforms and client platform 
				System.out.println("4) start SP, wait for connection from provider platforms and client platform");
				IExternalAccess	sp	= createPlatform(spconf);
				
				// when not found with awa -> should now receive the two network services and (if no ssp) the two global services from query.
				if(!awa)
				{
					checkNextResultAndAdd(client, resultmap, results, pro1.getId(), pro2.getId());
					checkNextResultAndAdd(client, resultmap, results, pro1.getId(), pro2.getId());
					if(sspconf==null)
					{
						checkNextResultAndAdd(client, resultmap, results, pro1.getId(), pro2.getId());
						checkNextResultAndAdd(client, resultmap, results, pro1.getId(), pro2.getId());
					}
					
					Assert.assertEquals("Should find two services: "+resultmap.get(pro1.getId()), 2, resultmap.get(pro1.getId()).size());
					Assert.assertEquals("Should find two services: "+resultmap.get(pro2.getId()), 2, resultmap.get(pro2.getId()).size());
				}
				else
				{
					// wait for connection to make sure that awa is off
					waitForSuperpeerConnections(sp, client);
				}
				
				// 5) add second query -> wait for two services (test if works when already SP)
				System.out.println("5) add second query");
				ISubscriptionIntermediateFuture<ITestService>	results2	= client.addQuery(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL));
				Map<IComponentIdentifier, Set<IServiceIdentifier>>	resultmap2	= new LinkedHashMap<>();
				
				checkNextResultAndAdd(client, resultmap2, results2, pro1.getId(), pro2.getId());
				checkNextResultAndAdd(client, resultmap2, results2, pro1.getId(), pro2.getId());
				
				// no more awa -> should find global services only when SSP available or global services cached in local SP
				if(sspconf!=null || SuperpeerClientAgent.SPCACHE)
				{
					checkNextResultAndAdd(client, resultmap2, results2, pro1.getId(), pro2.getId());
					checkNextResultAndAdd(client, resultmap2, results2, pro1.getId(), pro2.getId());
					
					Assert.assertEquals("Should find two services: "+resultmap2.get(pro1.getId()), 2, resultmap2.get(pro1.getId()).size());
					Assert.assertEquals("Should find two services: "+resultmap2.get(pro2.getId()), 2, resultmap2.get(pro2.getId()).size());
				}
				else
				{
					Assert.assertEquals("Should find one service: "+resultmap2.get(pro1.getId()), 1, resultmap2.get(pro1.getId()).size());
					Assert.assertEquals("Should find one service: "+resultmap2.get(pro2.getId()), 1, resultmap2.get(pro2.getId()).size());
				}
				
				// 6) start provider platform, wait for service in both queries -> test if works for existing queries (before and after SP)
				System.out.println("6) start remote platform, wait for service in both queries");
				IExternalAccess	pro3	= createPlatform(proconf);
				checkNextResultAndAdd(client, resultmap, results, pro3.getId());
				checkNextResultAndAdd(client, resultmap2, results2, pro3.getId());
				
				// no more awa -> should find global services only when SSP available or global services cached in local SP
				if(sspconf!=null || SuperpeerClientAgent.SPCACHE)
				{
					checkNextResultAndAdd(client, resultmap, results, pro3.getId());
					checkNextResultAndAdd(client, resultmap2, results2, pro3.getId());
				}
		
				// 7) kill SP, start provider platform, wait for service on both queries
				System.out.println("7) kill SP, start remote platform, wait for service on both queries");
				removePlatform(sp);
				if(awa && sspconf==null && !SuperpeerClientAgent.SPCACHE)
				{
					// After fallback to awa -> third global service is now found on first query
					checkNextResultAndAdd(client, resultmap, results, pro3.getId());
					
					// After fallback to awa -> all global services are now found on second query
					checkNextResultAndAdd(client, resultmap2, results2, pro1.getId(), pro2.getId(), pro3.getId());
					checkNextResultAndAdd(client, resultmap2, results2, pro1.getId(), pro2.getId(), pro3.getId());
					checkNextResultAndAdd(client, resultmap2, results2, pro1.getId(), pro2.getId(), pro3.getId());
					
					// All six services should be found in second query
					Assert.assertEquals("Should find two services: "+resultmap2.get(pro1.getId()), 2, resultmap2.get(pro1.getId()).size());
					Assert.assertEquals("Should find two services: "+resultmap2.get(pro2.getId()), 2, resultmap2.get(pro2.getId()).size());
					Assert.assertEquals("Should find two services: "+resultmap2.get(pro3.getId()), 2, resultmap2.get(pro3.getId()).size());
				}
				
				IExternalAccess	pro4 = createPlatform(proconf);
				if(awa)
				{
					// -> test if re-fallback to awa works for queries
					checkNextResultAndAdd(client, resultmap, results, pro4.getId());
					checkNextResultAndAdd(client, resultmap, results, pro4.getId());
					checkNextResultAndAdd(client, resultmap2, results2, pro4.getId());
					checkNextResultAndAdd(client, resultmap2, results2, pro4.getId());
					Assert.assertEquals("Should find two services: "+resultmap.get(pro4.getId()), 2, resultmap.get(pro4.getId()).size());
					Assert.assertEquals("Should find two services: "+resultmap2.get(pro4.getId()), 2, resultmap2.get(pro4.getId()).size());
				}
				else if(sspconf!=null)
				{
					// -> test if disconnection from SP works (only global services found in SSP)
					checkNextResultAndAdd(client, resultmap, results, pro4.getId());
					checkNextResultAndAdd(client, resultmap2, results2, pro4.getId());
					Assert.assertEquals("Should find one service: "+resultmap.get(pro4.getId()), 1, resultmap.get(pro4.getId()).size());
					Assert.assertEquals("Should find one service: "+resultmap2.get(pro4.getId()), 1, resultmap2.get(pro4.getId()).size());
				}
				// else not supported, class requires at least awa or ssp to work properly
			}
		});
	}

	/**
	 *  Collect and test a query result.
	 *  @param resultmap	collected platform id -> set of service ids.
	 *  @param svc	The newly found service.
	 */
	protected <T> void checkNextResultAndAdd(IExternalAccess client, Map<IComponentIdentifier, Set<IServiceIdentifier>> resultmap, IIntermediateFuture<T> fut, IComponentIdentifier... ids)
	{
		IServiceIdentifier	sid	= ((IService)fut.getNextIntermediateResult(Starter.getScaledDefaultTimeout(client.getId(), 3), Starter.isRealtimeTimeout(client.getId(), true))).getServiceId();
		IComponentIdentifier	cid	= sid.getProviderId().getRoot();
		if(!resultmap.containsKey(cid))
		{
			resultmap.put(cid, new LinkedHashSet<>());
		}
		Assert.assertTrue("Service should not be received twice: "+resultmap.get(cid)+", "+sid, resultmap.get(cid).add(sid));
		
		if(ids.length>0)
		{
			Assert.assertTrue("Service should be from patform(s): "+Arrays.asList(ids), Arrays.asList(ids).contains(cid));
		}
	}
	
	/**
	 *  cases for testing: services
	 */
	@Test
	public void	testServices()
	{
		// SSP is optional (TODO: support ssp started after client)
//		IExternalAccess	ssp	= sspconf!=null ? createPlatform(sspconf) : null;
		STest.runSimLocked(sspconf!=null ? sspconf : clientconf, ia0 ->
		{
			//-------- Tests with awareness fallback only (no SP) --------
			
			// 1) start client platform and search for service -> not found (test if works with no super peers and no other platforms)
			System.out.println("1) start client platform and search for service");
			IExternalAccess	ssp	= null;
			IExternalAccess	client	= null;
			if(sspconf!=null)
			{
				ssp	= ia0.getExternalAccess();
				client	= createPlatform(clientconf);				
			}
			else
			{
				client	= ia0.getExternalAccess();
			}
			waitForRegistryClient(client, true);
			Collection<ITestService>	result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
			Assert.assertTrue(""+result, result.isEmpty());
			
			IExternalAccess	pro1, pro2;
			if(awa)
			{
				// 2) start provider platform, search for service -> test if awa fallback works with one platform 
				System.out.println("2) start provider platform, search for service");
				pro1	= createPlatform(proconf);
				waitForRegistryWithProvider(client, pro1, true);
				result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
				Assert.assertEquals(""+result, 2, result.size()); // global + network provider
				
				// 3) start provider platform, search for service -> test if awa fallback works with two platforms 
				System.out.println("3) start provider platform, search for service");
				pro2	= createPlatform(proconf);
				waitForRegistryWithProvider(client, pro2, true);
				result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
				Assert.assertEquals(""+result, 4, result.size());
				
				// 3b) search by provider (must deliver scope and network services)
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
			else	// if sspconf!=null
			{
				// -> test if platforms don't see each other without SP.
				System.out.println("2/3) start provider platforms, wait for services");
				pro1	= createPlatform(proconf);
				pro2	= createPlatform(proconf);
				waitForRegistryWithProvider(client, pro1, true);
				waitForRegistryWithProvider(client, pro2, true);
				result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
				Assert.assertEquals(2, result.size());
				
				// 4) kill one provider platform, search for service -> test if platform is removed from global registry (if any)
				System.out.println("4) kill one provider platform, search for service");
				waitForProviderDisconnection(pro1, false, ssp);
				result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
				Assert.assertEquals(client.toString()+": "+result, 1, result.size());
			}
	
			//-------- Tests with SP if any --------
			
			if(spconf!=null)
			{
				// 5) start SP, wait for connection from provider platforms and client platform, search for service -> test if SP connection works
				System.out.println("5) start SP, wait for connection from provider platforms and client platform, search for service");
				IExternalAccess	sp	= createPlatform(spconf);
				waitForSuperpeerConnections(sp, client, pro2);
				waitForRegistryWithProvider(client, pro2, false);
				int	num	= sspconf==null && !SuperpeerClientAgent.SPCACHE ? 1:2;
				// retry at most 10 times until services are found
				// hack? pro2 services should be in registry after waitForRegistryWithProvider
				for(int i=0; i<=10; i++)
				{
					if(i>0) waitALittle(client);
					result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
					if(result.size()>=num) break;
					System.out.println("5"+(char)('a'+i)+") results: "+result.size()+", "+result);
				}
				Assert.assertEquals(client.toString()+": "+result, num, result.size());
	
				
				// 6) start provider platform, wait for connection, search for service -> test if search works for new platform and existing SP
				System.out.println("6) start provider platform, search for service");
				pro1	= createPlatform(proconf);
				waitForSuperpeerConnections(sp, pro1);
				waitForRegistryClient(client, false);
				num	= sspconf==null && !SuperpeerClientAgent.SPCACHE ? 2 : 4;
				// retry at most 10 times until services are found
				// hack? pro1 services should be in registry after waitForRegistryWithProvider
				for(int i=0; i<=10; i++)
				{
					if(i>0) waitALittle(client);
					result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
					if(result.size()>=num) break;
					System.out.println("6"+(char)('a'+i)+") results: "+result.size()+", "+result);
				}
				Assert.assertEquals("found: "+result+", new platform: "+pro1.getId(), num, result.size());
				
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
				waitForProviderDisconnection(pro1, false, sp, ssp);
				num	= sspconf==null && !SuperpeerClientAgent.SPCACHE ? 1 : 2;	// expected number of remaining services
				result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
				Assert.assertEquals(""+result, num, result.size());
		
				// 8) kill SP, search for service -> test if re-fallback to awa works
				System.out.println("8) kill SP, search for service");
				IFuture<Void>	conlost	= client.getExternalAccess(new ComponentIdentifier("superpeerclient", client.getId())).scheduleStep(ia ->
				{
					// Wait until SP connection is lost on client.
					Future<Void>	ret	= new Future<>();
					SuperpeerClientAgent	sca	= (SuperpeerClientAgent)ia.getFeature(IPojoComponentFeature.class).getPojoAgent();
					sca.getSPConnection(sp.getId()).addResultListener(new IntermediateDefaultResultListener<Void>()
					{
						@Override
						public void exceptionOccurred(Exception exception)
						{
							System.out.println("SP connection ended: "+exception);
							ret.setResult(null);
						}
						
						@Override
						public void finished()
						{
							System.out.println("SP connection ended.");
							ret.setResult(null);
						}
					});
					return ret;
				});
				removePlatform(sp);
				conlost.get();
				result	= client.searchServices(new ServiceQuery<>(ITestService.class, ServiceScope.GLOBAL)).get();
				Assert.assertEquals(""+result, awa? 2: 1, result.size());
			}
		});
	}
	
	//-------- helper methods --------
	
	/**
	 *  Kill a provider and wait for disconnection at super peers.
	 */
	protected void waitForProviderDisconnection(IExternalAccess provider, boolean dirty, IExternalAccess... sps)
	{
		// Listen to disconnection event of provider platform at SP and SSP.
		FutureBarrier<Void> disconnecteds = new FutureBarrier<Void>();
		for(IExternalAccess sp: sps)
		{
			if(sp!=null)	// allow null sp (e.g. ssp)
			{
				waitForSuperpeerConnections(sp, provider);
				Future<Void>	disconnected	= new Future<>();
				disconnecteds.addFuture(disconnected);
				sp.getExternalAccess(new ComponentIdentifier("superpeer", sp.getId())).scheduleStep(ia ->
				{
					SuperpeerRegistryAgent	spr	= (SuperpeerRegistryAgent) ia.getFeature(IPojoComponentFeature.class).getPojoAgent();
					spr.whenDisconnected(new ComponentIdentifier("superpeerclient", provider.getId())).addResultListener(new DelegationResultListener<Void>(disconnected));
//					{
//						public void	exceptionOccurred(Exception e)
//						{
//							System.out.println("Ignoring disconnection exception: "+e);
//							disconnected.setResult(null);
//						}
//					});
					return IFuture.DONE;
				}).get();
			}
		}
		
		// Connection timeout, i.e. wait time after which the SP should notice that the provider is gone.
		long	timeout	= 3 * Starter.getScaledDefaultTimeout(provider.getId(), (double) proconf.getValue("superpeerclient.contimeout", null));
		
		String	tokill	= dirty ? "intramvm" : "superpeerclient";
		System.out.println("Killing "+tokill+"agent of provider...");
		provider.killComponents(new ComponentIdentifier(tokill, provider.getId())).get();
		removePlatform(provider);
		
		System.out.println("Waiting for provider->sp disconnection: "+timeout);
		disconnecteds.waitFor().get(timeout);
	}

	/**
	 *  Wait to allow remote platform/registry interaction.
	 *  The idea is that the registry is roughly FCFS so
	 *  when we start another service and that service appears in the registry
	 *  all previous activity should also be completed.
	 */
	protected void waitForRegistryClient(IExternalAccess client, boolean global)
	{
//		Logger.getLogger(getClass().getName()).info("waitForRegistryClient0: "+client+", "+true);
		// Can only use global when ssp available. Otherwise uses awa fallback via network.
		global	= global && sspconf!=null;
		
		IExternalAccess	marker	= Starter.createPlatform(clientconf).get();
		ISubscriptionIntermediateFuture<IMarkerService>	sub	= client.addQuery(new ServiceQuery<>(IMarkerService.class, global ? ServiceScope.GLOBAL : ServiceScope.NETWORK));
		IExternalAccess	agent	= marker.addComponent(global ? new GlobalMarkerAgent() : new NetworkMarkerAgent()).get();
		IComponentIdentifier	found;
		do
		{
			// TODO: use listener instead of blocking API to exclude API as heisenbug cause
			found	= ((IService)sub.getNextIntermediateResult(Starter.getScaledDefaultTimeout(client.getId(), 3), Starter.isRealtimeTimeout(client.getId(), true))).getServiceId().getProviderId();
//			Logger.getLogger(getClass().getName()).info("Found marker: "+found+"; expecting: "+agent.getId()+", "+agent.getId().equals(found));
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
		long	to	= Starter.getScaledDefaultTimeout(client.getId(), 3);
		boolean	rt	= Starter.isRealtimeTimeout(client.getId(), true);
		while(!agent.getId().equals(((IService)sub.getNextIntermediateResult(to, rt)).getServiceId().getProviderId()))
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
