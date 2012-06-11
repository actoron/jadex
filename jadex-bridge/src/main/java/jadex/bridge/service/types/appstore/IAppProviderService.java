package jadex.bridge.service.types.appstore;

import jadex.commons.future.IFuture;

/**
 *  Interface for applications that want to provider
 *  themselves as app in the store.
 */
public interface IAppProviderService<T>
{
//	/** Constant for swing gui. */
//	public final static String GUI_TYPE_SWING = "Swing"; 
//	
//	/** Constant for android gui. */
//	public final static String GUI_TYPE_ANDROID = "Android"; 
	
	/**
	 * 
	 */
	public IFuture<AppMetaInfo> getAppMetaInfo();

	/**
	 * 
	 */
//	public IFuture<Class<? extends IAppGui<T>>> getApplicationGui(String guitype);
	
	/**
	 *  Get the application instance as entrance point.
	 *  @return The application.
	 */
	public IFuture<T> getApplication();
	
}
