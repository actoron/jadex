package jadex.launch.test.remotereference;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;

/**
 *  Test if a remote references are correctly transferred and mapped back.
 */
public class RemoteReference2Test //extends TestCase
{
	@Rule 
	public TestName name = new TestName();

	
	@Test
	public void	testRemoteReference()
	{
		long timeout	= Starter.getLocalDefaultTimeout(null);
		
		// Start platform1 used for remote access.
		final IExternalAccess	platform1	= Starter.createPlatform(STest.getDefaultTestConfig()).get(timeout);
		timeout	= Starter.getLocalDefaultTimeout(platform1.getComponentIdentifier());
		
		// Start platform2 with services.
		IPlatformConfiguration	config2	= STest.getDefaultTestConfig();
		config2.addComponent(SearchServiceProviderAgent.class);		
		config2.addComponent(LocalServiceProviderAgent.class);		
		IExternalAccess	platform2	= Starter.createPlatform(config2).get(timeout);
		
		// Connect platforms by creating proxy agents.
		Starter.createProxy(platform1, platform2).get(timeout);
		Starter.createProxy(platform2, platform1).get(timeout);
		
		// Find local service with direct remote search.
		System.out.println("searching local");
		ILocalService	service1	= SServiceProvider
			.getService(platform1, ILocalService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(timeout);
//		ILocalService	service1 = SServiceProvider.waitForService(platform1, new IResultCommand<IFuture<ILocalService>, Void>()
//		{
//			public IFuture<ILocalService> execute(Void args)
//			{
//				return SServiceProvider.searchService(platform1, new ServiceQuery<>( ILocalService.class, RequiredServiceInfo.SCOPE_GLOBAL));
//			}
//		}, 7, 1500).get();
		
		// Search for remote search service from local platform
		System.out.println("searching global");
		ISearchService	search	= SServiceProvider
			.getService(platform1, ISearchService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(timeout);
		// Search for remote search service from local platform
//		ISearchService	search = SServiceProvider.waitForService(platform1, new IResultCommand<IFuture<ISearchService>, Void>()
//		{
//			public IFuture<ISearchService> execute(Void args)
//			{
//				return SServiceProvider.searchService(platform1, new ServiceQuery<>( ISearchService.class, RequiredServiceInfo.SCOPE_GLOBAL));
//			}
//		}, 7, 1500).get();
		
		// Invoke service to obtain reference to local service.
		System.out.println("searching through service");
		ILocalService	service2	= search.searchService("dummy").get(timeout);
		
		// Remote reference should be mapped back to remote provided service proxy.
		System.out.println("done: "+service1+", "+service2);
		Assert.assertEquals(service1, service2);

		// Kill platforms and end test case.
		platform2.killComponent().get(timeout);
		platform1.killComponent().get(timeout);
	}
}
