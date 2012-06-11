package jadex.bridge.service.types.appstore;

import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;

/**
 *  Interface for applications that want to provider
 *  themselves as app in the store.
 */
public interface IAppProviderService
{
	/**
	 * 
	 */
	public IFuture<String> getApplicationName();

	/**
	 * 
	 */
	public IFuture<String> getApplicationDescription();

	/**
	 * 
	 */
	public IFuture<Class<?>> getApplicationPanel();

	
	
}
