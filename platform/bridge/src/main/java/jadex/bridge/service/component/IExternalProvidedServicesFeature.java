package jadex.bridge.service.component;

import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.commons.future.IFuture;

/**
 *  Component feature for provided services.
 */
public interface IExternalProvidedServicesFeature
{
	/**
	 *  Add a service to the container.
	 *  The service is started, if the container is already running.
	 *  @param service The service.
	 *  @param info The provided service info.
	 *  @return A future that is done when the service has completed starting.  
	 */
//	public void addService(IInternalService service, ProvidedServiceInfo info);
	public IFuture<Void> addService(String name, Class<?> type, Object service);

	/**
	 *  Add a service to the platform.
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param type The public service interface.
	 *  @param service The service.
	 *  @param type The proxy type (@see{BasicServiceInvocationHandler}).
	 */
	public IFuture<Void> addService(String name, Class<?> type, Object service, String proxytype);
	
	/**
	 *  Add a service to the platform. 
	 *  If under the same name and type a service was contained,
	 *  the old one is removed and shutdowned.
	 *  @param type The public service interface.
	 *  @param service The service.
	 *  @param scope	The service scope.
	 */
	public IFuture<Void> addService(String name, Class<?> type, Object service, PublishInfo pi, String scope);
	
	/**
	 *  Sets the tags of a service.
	 *  
	 *  @param sid The Service identifier.
	 *  @param tags The tags.
	 *  @return New service identifier.
	 */
	public IFuture<Void> setTags(IServiceIdentifier sid, String... tags);
	
	/**
	 *  Removes a service from the container (shutdowns also the service if the container is running).
	 *  @param service The service identifier.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture<Void> removeService(IServiceIdentifier sid);
		
}
