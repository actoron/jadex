package jadex.bridge.service.types.remote;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.IFuture;


/**
 *  Interface for remote management service.
 */
public interface IRemoteServiceManagementService
{	
	/**
	 *  Get a service proxy from a remote component.
	 *  (called from arbitrary components)
	 *  @param caller	The component that started the search.
	 *  @param cid	The remote provider id.
	 *  @param service	The service type.
	 *  @param scope	The search scope. 
	 *  @return The service proxy.
	 */
	public <T> IFuture<T> getServiceProxy(IComponentIdentifier caller, IComponentIdentifier cid, Class<T> service, String scope, IAsyncFilter<T> filter);
	
	/**
	 *  Get all service proxies from a remote component.
	 *  (called from arbitrary components)
	 *  @param caller	The component that started the search.
	 *  @param cid	The remote provider id.
	 *  @param service	The service type.
	 *  @param scope	The search scope. 
	 *  @return The service proxy.
	 */
	public <T> IFuture<Collection<T>> getServiceProxies(IComponentIdentifier caller, IComponentIdentifier cid, Class<T> service, String scope, IAsyncFilter<T> filter);

	/**
	 *  Get an external access proxy from a remote component.
	 *  (called from arbitrary components)
	 *  @param cid Component target id.
	 *  @return External access of remote component. 
	 */
	public IFuture<IExternalAccess> getExternalAccessProxy(IComponentIdentifier cid);
}
