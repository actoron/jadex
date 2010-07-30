package jadex.bridge;

import jadex.commons.IFuture;
import jadex.service.IResultSelector;
import jadex.service.ISearchManager;
import jadex.service.IVisitDecider;


/**
 *  Interface for remote management service.
 */
public interface IRemoteServiceManagementService
{	
	/**
	 *  Get a service proxies from a remote platform.
	 *  (called from arbitrary components)
	 *  @param rms The remote management component identifier (target platform).
	 *  @param providerid Optional component id that is used to start the search.
	 *  @param manager The search manager.
	 *  @param decider The visit decider.
	 *  @param selector The result selector.
	 *  @return Collection or single result (i.e. service proxies). 
	 */
	public IFuture getServiceProxies(IComponentIdentifier rms, Object providerid, 
		ISearchManager manager, IVisitDecider decider, IResultSelector selector);
	
	/**
	 *  Get a service proxy from a remote platform.
	 *  (called from arbitrary components)
	 *  @param platform The component id of the remote platform.
	 *  @param service The service type.
	 *  @return The service proxy.
	 */
	public IFuture getServiceProxy(IComponentIdentifier platform, Class service);
	
	/**
	 *  Get all service proxies from a remote platform.
	 *  (called from arbitrary components)
	 *  @param platform The component id of the remote platform.
	 *  @param service The service type.
	 *  @return The service proxy.
	 */
	public IFuture getServiceProxies(IComponentIdentifier platform, Class service);
}
