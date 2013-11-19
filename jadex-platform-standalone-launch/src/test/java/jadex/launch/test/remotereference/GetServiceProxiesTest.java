package jadex.launch.test.remotereference;

import jadex.base.Starter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.chat.IChatService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.ThreadSuspendable;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 *  Try to find a bug in remote future termination.
 */
public class GetServiceProxiesTest extends TestCase
{
	public void	testGetServiceProxies() throws Exception
	{
		long timeout	= 30000;
		ISuspendable	sus	= 	new ThreadSuspendable();
		
		// Start platform1 (underscore in name assures both platforms use same password)
		IExternalAccess	platform1	= Starter.createPlatform(new String[]{"-platformname", "testcases_*",
			"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false"}).get(sus, timeout);
		
		for(int i=1; i<=10; i++)
		{
			System.out.println("run "+i);
			// Start platform2 (underscore in name assures both platforms use same password)
			IExternalAccess	platform2	= Starter.createPlatform(new String[]{"-platformname", "testcases_*",
				"-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false"}).get(sus, timeout);
			
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
			
			// Search for remote chat service from local platform
			ITerminableIntermediateFuture<IChatService>	search1	= SServiceProvider
				.getServices(platform1.getServiceProvider(), IChatService.class, RequiredServiceInfo.SCOPE_GLOBAL);
			ITerminableIntermediateFuture<IChatService>	search2	= SServiceProvider
				.getServices(platform2.getServiceProvider(), IChatService.class, RequiredServiceInfo.SCOPE_GLOBAL);
			
			// Search for remote chat service from local platform
			IFuture<IChatService>	search3	= SServiceProvider
				.getService(platform1.getServiceProvider(), new ComponentIdentifier("chat", platform2.getComponentIdentifier()), IChatService.class);
			IFuture<IChatService>	search4	= SServiceProvider
				.getService(platform2.getServiceProvider(), new ComponentIdentifier("chat", platform1.getComponentIdentifier()), IChatService.class);

//			Thread.sleep(50);
			
			// Kill platform while searching.
			platform2.killComponent().get(sus, timeout);
		}

		platform1.killComponent().get(sus, timeout);
	}
	
	/**
	 *  Execute in main to have no timeouts.
	 */
	public static void main(String[] args) throws Exception
	{
		GetServiceProxiesTest test = new GetServiceProxiesTest();
		test.testGetServiceProxies();
	}
}
