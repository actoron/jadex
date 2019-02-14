package jadex.launch.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import jadex.base.Starter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.address.ITransportAddressService;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.cms.CreationInfo;

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
		String[] platformargs = new String[]
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
		String remote_name = "test_remote";
		String remote_addr = "tcp://localhost:12345";
		IComponentIdentifier remote_cid = new ComponentIdentifier(remote_name);//, new String[]{remote_addr});
		
		// Start local platform
		String[] platformargs = new String[]
		{
			"-awareness", "false",
			"-relay", "false",
			"-platformname", "test_local"
			//"-logging", "true"
		};
		IExternalAccess	platform = Starter.createPlatform(platformargs).get();
		ITransportAddressService tas = platform.searchService(new ServiceQuery<ITransportAddressService>(ITransportAddressService.class)).get();
		tas.addManualAddresses(Arrays.asList(new TransportAddress(new ComponentIdentifier(remote_name), remote_addr)));
		
		// Create proxy for remote platform such that remote services are found
		Map<String, Object>	args = new HashMap<String, Object>();
		args.put("component", remote_cid);
		CreationInfo ci = new CreationInfo(args).setFilename("jadex/platform/service/remote/ProxyAgent.class");
		platform.createComponent(ci).get();
	}
	
	public static void	main(String[] args)
	{
		// Order does not matter, i.e. remote platform does not need to run, when the connection is set up - only when services are searched.
		createAndConnectLocalPlatform();
		createRemotePlatform();
	}
}
