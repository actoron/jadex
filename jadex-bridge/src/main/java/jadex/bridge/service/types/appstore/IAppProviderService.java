package jadex.bridge.service.types.appstore;

import jadex.bridge.service.annotation.Security;
import jadex.commons.future.IFuture;

/**
 *  Interface for applications that want to provider
 *  themselves as app in the store.
 */
@Security(roles=Security.UNRESTRICTED)
public interface IAppProviderService<T>
{
	/**
	 *  Get meta information about an application.
	 *  @return The meta info.
	 */
	public IFuture<AppMetaInfo> getAppMetaInfo();

	/**
	 *  Get the application instance as entrance point.
	 *  @return The application.
	 */
	public IFuture<T> getApplication();
	
}
