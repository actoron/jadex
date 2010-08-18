package jadex.bridge;

import jadex.commons.IFuture;
import jadex.service.IResultSelector;
import jadex.service.ISearchManager;
import jadex.service.IService;
import jadex.service.IVisitDecider;


/**
 *  Interface for remote management service.
 */
public interface IRemoteServiceManagementService extends IService
{	
	/**
	 *  Get service proxies from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid Component id that is used to start the search.
	 *  @param manager The search manager.
	 *  @param decider The visit decider.
	 *  @param selector The result selector.
	 *  @return Collection or single result (i.e. service proxies). 
	 */
	public IFuture getServiceProxies(IComponentIdentifier cid, 
		ISearchManager manager, IVisitDecider decider, IResultSelector selector);
	
	/**
	 *  Get a service proxy from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid The remote provider id.
	 *  @param service The service type.
	 *  @return The service proxy.
	 */
	public IFuture getServiceProxy(IComponentIdentifier cid, Class service);
	
	/**
	 *  Get all service proxies from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid The remote provider id.
	 *  @param service The service type.
	 *  @return The service proxy.
	 */
	public IFuture getServiceProxies(IComponentIdentifier cid, Class service);

	/**
	 *  Get all declared service proxies from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid The remote provider id.
	 *  @param service The service type.
	 *  @return The service proxy.
	 */
	public IFuture getDeclaredServiceProxies(IComponentIdentifier cid);
	
	/**
	 *  Get an external access proxy from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid Component target id.
	 *  @return External access of remote component. 
	 */
	public IFuture getExternalAccessProxy(IComponentIdentifier cid);
	
}
