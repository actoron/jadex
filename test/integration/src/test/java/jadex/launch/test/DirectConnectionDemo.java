package jadex.launch.test;

import java.util.HashMap;
import java.util.Map;

import jadex.base.Starter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.micro.annotation.Binding;

/**
 *  Create a direct connection between to another platform without awareness.
 */
// Not a test case, just demo code.
public class DirectConnectionDemo
{
	/**
	 *  Create a platform to connect to.
	 */
	private static void	createRemotePlatform()
	{
		String[]	platformargs	= new String[]
		{
			"-awareness", "false",
			"-relay", "false",
			"-tcpport", "12345",
			"-platformname", "test_remote"
		};
		Starter.createPlatform(platformargs).get();
	}
	
	/**
	 *  Create a platform and connect it to another platform.
	 */
	private static void	createAndConnectLocalPlatform()
	{
		// Address of remote platform
		String	remote_name	= "test_remote";
		String	remote_addr	= "tcp-mtp://localhost:12345";
		IComponentIdentifier	remote_cid	= new ComponentIdentifier(remote_name, new String[]{remote_addr});
		
		// Start local platform
		String[]	platformargs	= new String[]
		{
			"-awareness", "false",
			"-relay", "false",
			"-platformname", "test_local"
		};
		IExternalAccess	platform	= Starter.createPlatform(platformargs).get();
		
		
		// Create proxy for remote platform such that remote services are found
		Map<String, Object>	args = new HashMap<String, Object>();
		args.put("component", remote_cid);
		CreationInfo ci = new CreationInfo(args);
		IComponentManagementService	cms	= SServiceProvider.searchService(platform, new ServiceQuery<>( IComponentManagementService.class, Binding.SCOPE_PLATFORM)).get();
		cms.createComponent("jadex/platform/service/remote/ProxyAgent.class", ci).getFirstResult();
	}
	
	public static void	main(String[] args)
	{
		// Order does not matter, i.e. remote platform does not need to run, when the connection is set up - only when services are searched.
		createAndConnectLocalPlatform();
		createRemotePlatform();
	}
}
