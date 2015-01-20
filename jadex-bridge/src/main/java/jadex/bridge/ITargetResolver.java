package jadex.bridge;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ITargetResolver 
{
	/**
	 * 
	 */
//	public IFuture<ProxyReference> determineTarget(ProxyReference oldtarget, RemoteServiceManagementService rsms);
	
	/**
	 * 
	 */
	public IFuture<IService> determineTarget(IComponentIdentifier rms, IServiceIdentifier sid, IExternalAccess agent);
}
