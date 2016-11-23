package jadex.platform.service.awareness.discovery;

import java.util.HashMap;
import java.util.Map;

import jadex.base.Starter;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.micro.annotation.Binding;

/**
 *  Class helping with manual connects to remote platforms.
 *
 */
public class SConnect
{
	/**
	 *  Manually connects to a remote platform.
	 *  
	 *  @param platformname Name of the platform.
	 *  @param host Host name.
	 *  @param port Port.
	 */
	public static final void connectPlatformTcp(IInternalAccess component, String platformname, String host, String port)
	{
		connectPlatform(component, platformname, "tcp-mtp://"+host+":"+port);
	}
	
	/**
	 *  Manually connects to a remote platform.
	 *  
	 *  @param platformname Name of the platform.
	 *  @param remoteaddr Transport URL.
	 */
	public static final void connectPlatform(IInternalAccess component, String platformname, String remoteaddr)
	{
		// Address of remote platform
		IComponentIdentifier	remotecid	= new ComponentIdentifier(platformname, new String[]{remoteaddr});
		
		// Create proxy for remote platform such that remote services are found
		Map<String, Object>	args = new HashMap<String, Object>();
		args.put("component", remotecid);
		CreationInfo ci = new CreationInfo(args);
		ci.setDaemon(true);
		IComponentManagementService	cms	= SServiceProvider.getLocalService(component, IComponentManagementService.class, Binding.SCOPE_PLATFORM);
		cms.createComponent(platformname, "jadex/platform/service/remote/ProxyAgent.class", ci).getFirstResult();
	}
}
