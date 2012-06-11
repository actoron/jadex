package jadex.bridge.service.types.appstore;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IAppGui<T>
{
	/**
	 * 
	 */
	public IFuture<Void> init(IExternalAccess agent, T service);
	
	/**
	 * 
	 */
	public IFuture<Void> shutdown();
}
