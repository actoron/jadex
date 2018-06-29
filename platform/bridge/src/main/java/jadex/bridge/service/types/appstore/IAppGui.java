package jadex.bridge.service.types.appstore;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IAppGui
{
	/**
	 * 
	 */
	public IFuture<Void> init(IExternalAccess agent, IService service);
	
	/**
	 * 
	 */
	public IFuture<Void> shutdown();
}
