package jadex.launch.test.remotereference;

import org.junit.Assert;
import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;

/**
 *  Test if a remote references are correctly transferred and mapped back.
 */
public class RemoteReference2Test //extends TestCase
{
	/**
	 *  Run the test.
	 */
	@Test
	public void	testRemoteReference()
	{
		long timeout	= Starter.getDefaultTimeout(null);
		
		// Start platform1 used for remote access.
		final IExternalAccess	platform1	= Starter.createPlatform(STest.getDefaultTestConfig()).get(timeout);
		timeout	= Starter.getDefaultTimeout(platform1.getId());
		
		// Start platform2 with services.
		IPlatformConfiguration	config2	= STest.getDefaultTestConfig();
		config2.addComponent(SearchServiceProviderAgent.class);		
		config2.addComponent(LocalServiceProviderAgent.class);		
		IExternalAccess	platform2	= Starter.createPlatform(config2).get(timeout);
		
		// Find local service with direct remote search.
		System.out.println("searching local");
		ILocalService	service1	= platform1.searchService( new ServiceQuery<>(ILocalService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get(timeout);
		// Search for remote search service from local platform
		System.out.println("searching global");
		ISearchService	search	= platform1.searchService( new ServiceQuery<>(ISearchService.class, RequiredServiceInfo.SCOPE_GLOBAL)).get(timeout);
		
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
