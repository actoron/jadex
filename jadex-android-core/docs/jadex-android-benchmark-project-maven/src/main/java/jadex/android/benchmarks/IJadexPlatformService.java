package jadex.android.benchmarks;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;

/**
 *	Interface for the platform service.
 */
public interface IJadexPlatformService
{
	/**
	 *  Get the jadex platform.
	 */
	public IFuture<IExternalAccess>	getPlatform();
}
