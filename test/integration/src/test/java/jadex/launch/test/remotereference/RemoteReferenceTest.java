package jadex.launch.test.remotereference;

import org.junit.Assert;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;

/**
 *  Test if a remote references are correctly transferred and mapped back.
 *  
 *  On platform1 there is a serviceA provider.
 *  On platform2 there is a search component which searches for serviceA and return it as result.
 *  
 *  Tests if the result of the remote search yields the same local service proxy.
 */
public class RemoteReferenceTest //extends TestCase
{
	@Test
	public void	testRemoteReference()
	{
		long timeout = Starter.getLocalDefaultTimeout(null);
		
		// Start platform1 with local service.
		IPlatformConfiguration	config1	= STest.getDefaultTestConfig();
		config1.addComponent(LocalServiceProviderAgent.class);
		final IExternalAccess	platform1	= Starter.createPlatform(config1).get(timeout);
		timeout	= Starter.getLocalDefaultTimeout(platform1.getComponentIdentifier());
		
		// Find local service (as local provided service proxy).
		ILocalService	service1	= SServiceProvider
			.getService(platform1, ILocalService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(timeout);
		
		// Start platform2 with (remote) search service.
		IPlatformConfiguration	config2	= STest.getDefaultTestConfig();
		config2.addComponent(SearchServiceProviderAgent.class);
		IExternalAccess	platform2	= Starter.createPlatform(config2).get(timeout);
		
//		// Connect platforms by creating proxy agents.
//		Starter.createProxy(platform1, platform2).get(timeout);
//		Starter.createProxy(platform2, platform1).get(timeout);
		
		// Search for remote search service from local platform
		ISearchService	search = SServiceProvider.searchService(platform1, new ServiceQuery<>( ISearchService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get(timeout);

		// Invoke service to obtain reference to local service.
		ILocalService	service2	= search.searchService("dummy").get(timeout);
		
		// Remote reference should be mapped back to local provided service proxy.
		Assert.assertSame(service1, service2);

		// Kill platforms and end test case.
		platform2.killComponent().get(timeout);
		platform1.killComponent().get(timeout);
	}
	
	/**
	 *  Execute in main to have no timeouts.
	 */
	public static void main(String[] args)
	{
		RemoteReferenceTest test = new RemoteReferenceTest();
		for (int i = 0; i < 20; ++i)
		test.testRemoteReference();
	}
}
