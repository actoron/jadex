package jadex.launch.test.remotereference;

import org.junit.Assert;
import org.junit.Test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;

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
		
		// Start platform1 with local service. (underscore in name assures both platforms use same password)
		IExternalAccess	platform1	= Starter.createPlatform(new String[]{"-platformname", "testcases_*",
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false",
			"-gui", "false",
//			"-logging", "true",
			"-awareness", "false", "-printpass", "false",
			"-component", "jadex/launch/test/remotereference/LocalServiceProviderAgent.class"}).get(timeout);
		timeout	= Starter.getLocalDefaultTimeout(platform1.getComponentIdentifier());
		
		// Find local service (as local provided service proxy).
		ILocalService	service1	= SServiceProvider
			.getService(platform1, ILocalService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(timeout);
		
		// Start platform2 with (remote) search service. (underscore in name assures both platforms use same password)
		IExternalAccess	platform2	= Starter.createPlatform(new String[]{"-platformname", "testcases_*",
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
//			"-logging", "true",
			"-component", "jadex/launch/test/remotereference/SearchServiceProviderAgent.class"}).get(timeout);
		
		// Connect platforms by creating proxy agents.
		Starter.createProxy(platform1, platform2).get(timeout);
		Starter.createProxy(platform2, platform1).get(timeout);
		
		// Search for remote search service from local platform
		ISearchService	search	= SServiceProvider
			.getService(platform1, ISearchService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(timeout);
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
		test.testRemoteReference();
	}
}
