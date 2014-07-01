package jadex.launch.test.remotereference;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

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
		long timeout	= BasicService.getLocalDefaultTimeout();
		ISuspendable	sus	= 	new ThreadSuspendable();
		
		// Underscore in platform name assures both platforms use same password.
		String	pid	= SUtil.createUniqueId(name.getMethodName(), 3)+"-*";
		
		// Start platform1 used for remote access.
		IExternalAccess	platform1	= Starter.createPlatform(new String[]{"-platformname", pid,
//			"-relaytransport", "false",
//			"-deftimeout", Long.toString(timeout),
//			"-logging", "true",
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
			}).get(sus, timeout);
		
		// Start platform2 with services.
		IExternalAccess	platform2	= Starter.createPlatform(new String[]{"-platformname", pid,
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
//			"-relaytransport", "false",
//			"-deftimeout", Long.toString(timeout),
//			"-logging", "true",
			"-component", "jadex/launch/test/remotereference/SearchServiceProviderAgent.class",
			"-component", "jadex/launch/test/remotereference/LocalServiceProviderAgent.class"}).get(sus, timeout);
		
		// Connect platforms by creating proxy agents.
		Map<String, Object>	args1	= new HashMap<String, Object>();
		args1.put("component", platform2.getComponentIdentifier());
		IComponentManagementService	cms1	= SServiceProvider
			.getServiceUpwards(platform1.getServiceProvider(), IComponentManagementService.class).get(sus, timeout);
		cms1.createComponent(null, "jadex/platform/service/remote/ProxyAgent.class", new CreationInfo(args1), null).get(sus, timeout);
		Map<String, Object>	args2	= new HashMap<String, Object>();
		args2.put("component", platform1.getComponentIdentifier());
		IComponentManagementService	cms2	= SServiceProvider
			.getServiceUpwards(platform2.getServiceProvider(), IComponentManagementService.class).get(sus, timeout);
		cms2.createComponent(null, "jadex/platform/service/remote/ProxyAgent.class", new CreationInfo(args2), null).get(sus, timeout);
		
		// Find local service with direct remote search.
//		System.out.println("searching local");
		ILocalService	service1	= SServiceProvider
			.getService(platform1.getServiceProvider(), ILocalService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(sus, timeout);
		
		// Search for remote search service from local platform
//		System.out.println("searching global");
		ISearchService	search	= SServiceProvider
			.getService(platform1.getServiceProvider(), ISearchService.class, RequiredServiceInfo.SCOPE_GLOBAL).get(sus, timeout);
		// Invoke service to obtain reference to local service.
		ILocalService	service2	= search.searchService("dummy").get(sus, timeout);
		
		// Remote reference should be mapped back to remote provided service proxy.
		Assert.assertEquals(service1, service2);

		// Kill platforms and end test case.
		platform2.killComponent().get(sus, timeout);
		platform1.killComponent().get(sus, timeout);
	}
}
