package jadex.platform.remotereference;

import org.junit.Assert;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;

/**
 *  Test if a remote references are correctly transferred and mapped back.
 *  
 *  On platform1 there is a serviceA provider.
 *  On platform2 there is a search component which searches for serviceA and return it as result.
 *  
 *  Tests if the result of the remote search yields the same local service proxy.
 */
public class RemoteReferenceTest
{
	@Test
	public void	testRemoteReference()
	{
		IPlatformConfiguration	baseconf	= STest.createDefaultTestConfig(getClass());
		// Use larger timeout so we can reduce default timeout on build slave
		long timeout = Starter.getScaledDefaultTimeout(null, 5);
		
		// Start platform1 with local service.
		IPlatformConfiguration	config1	= baseconf.clone()
//			.setLogging(true)
			.addComponent(LocalServiceProviderAgent.class);
		final IExternalAccess	platform1	= Starter.createPlatform(config1).get(timeout);
		timeout	= Starter.getDefaultTimeout(platform1.getId());
		
		// Find local service (as local provided service proxy).
		ILocalService	service1	= platform1.searchService( new ServiceQuery<>(ILocalService.class, ServiceScope.PLATFORM)).get(timeout);
		
		// Start platform2 with (remote) search service.
		IPlatformConfiguration	config2	= baseconf.clone()
//			.setLogging(true)
			.addComponent(SearchServiceProviderAgent.class);
		IExternalAccess	platform2	= Starter.createPlatform(config2).get(timeout);
		
//		// Connect platforms by creating proxy agents.
//		Starter.createProxy(platform1, platform2).get(timeout);
//		Starter.createProxy(platform2, platform1).get(timeout);
		
		// Search for remote search service from local platform
		ISearchService	search = platform1.searchService( new ServiceQuery<>( ISearchService.class, ServiceScope.GLOBAL)).get(timeout);

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
