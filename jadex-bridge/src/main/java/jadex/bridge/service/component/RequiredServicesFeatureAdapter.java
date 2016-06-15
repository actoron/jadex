package jadex.bridge.service.component;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.IAsyncFilter;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITerminableIntermediateFuture;

/**
 *  Adapter for the required services feature.
 */
public class RequiredServicesFeatureAdapter implements IRequiredServicesFeature
{
	/** The delegate. */
	protected IRequiredServicesFeature delegate;
	
	/**
	 *  Create a new adapter.
	 */
	public RequiredServicesFeatureAdapter(IRequiredServicesFeature delegate)
	{
		this.delegate = delegate;
	}
	
	/**
	 *  Get the required service infos.
	 */
	public RequiredServiceInfo[] getRequiredServiceInfos()
	{
		return delegate.getRequiredServiceInfos();
	}
	
	/**
	 *  Get the required service info.
	 *  @param name The name.
	 *  @return The required service info.
	 */
	public RequiredServiceInfo getRequiredServiceInfo(String name)
	{
		return delegate.getRequiredServiceInfo(rename(name));
	}
	
	/**
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name)
	{
		return delegate.getRequiredService(rename(name));
	}
	
	/**
	 *  Get a required services of a given name.
	 *  @param name The services name.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name)
	{
		return delegate.getRequiredServices(rename(name));
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind)
	{
		return delegate.getRequiredService(rename(name), rebind);
	}
	
	/**
	 *  Get a required services.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind)
	{
		return delegate.getRequiredServices(rename(name), rebind);
	}
	
	/**
	 *  Get a required service.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind, IAsyncFilter<T> filter)
	{
		return delegate.getRequiredService(rename(name), rebind, filter);
	}
	
	/**
	 *  Get a required services.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind, IAsyncFilter<T> filter)
	{
		return delegate.getRequiredServices(rename(name), rebind, filter);
	}
	
	/**
	 *  Get a required service using tags.
	 *  @param name The required service name.
	 *  @param rebind If false caches results.
	 *  @param tags The service tags.
	 *  @return The service.
	 */
	public <T> IFuture<T> getRequiredService(String name, boolean rebind, String... tags)
	{
		return delegate.getRequiredService(name, rebind, tags);
	}
	
	/**
	 *  Get a required services using tags.
	 *  @param name The required service name.
	 *  @param rebind If false caches results.
	 *  @param tags The service tags.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> ITerminableIntermediateFuture<T> getRequiredServices(String name, boolean rebind, String... tags)
	{
		return delegate.getRequiredServices(name, rebind, tags);
	}
	
	/**
	 *  Get the result of the last search.
	 *  @param name The required service name.
	 *  @return The last result.
	 */
	public <T> T getLastRequiredService(String name)
	{
		return delegate.getLastRequiredService(rename(name));
	}
	
	/**
	 *  Get the result of the last search.
	 *  @param name The required services name.
	 *  @return The last result.
	 */
	public <T> Collection<T> getLastRequiredServices(String name)
	{
		return delegate.getLastRequiredServices(rename(name));
	}
	
	// extra methods for searching
	
	/**
	 *  Get one service of a type from a specific component.
	 *  @param type The class.
	 *  @param cid The component identifier of the target component.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchService(Class<T> type, IComponentIdentifier cid)
	{
		return delegate.searchService(type, cid);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchService(Class<T> type)
	{
		return delegate.searchService(type);
	}
	
	/**
	 *  Get one service of a type.
	 *  @param type The class.
	 *  @return The corresponding service.
	 */
	public <T> IFuture<T> searchService(Class<T> type, String scope)
	{
		return delegate.searchService(type, scope);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> IIntermediateFuture<T> searchServices(Class<T> type)
	{
		return delegate.searchServices(type);
	}
	
	/**
	 *  Get all services of a type.
	 *  @param type The class.
	 *  @return Each service as an intermediate result and a collection of services as final result.
	 */
	public <T> IIntermediateFuture<T> searchServices(Class<T> type, String scope)
	{
		return delegate.searchServices(type, scope);
	}
	
	/**
	 *  Rename if necessary.
	 */
	public String rename(String name)
	{
		return name;
	}
}
