package jadex.platform.service.globalservicepool;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.ITargetResolver;
import jadex.bridge.service.IServiceIdentifier;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  The service pool target resolver is the client side of a
 *  global pool manager. 
 *  It has the purpose the direct the call to a suitable service worker
 *  from the queue of workers.
 */
public class ServicePoolTargetResolver implements ITargetResolver
{
	/**
	 * 
	 */
	public IFuture<IServiceIdentifier> determineTarget(IComponentIdentifier rms, IServiceIdentifier sid, IExternalAccess agent)
//	public IFuture<ProxyReference> determineTarget(ProxyReference oldtarget, RemoteServiceManagementService rsms)
	{
		System.out.println("Called service pool resolver: "+sid);
		
		return new Future<IServiceIdentifier>(sid);
	}
}
