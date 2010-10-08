package jadex.tools.daemon;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.IFuture;

/**
 * 
 */
public interface IDaemonService
{
	/**
	 *  Start a platform using a configuration.
	 *  @param args The arguments.
	 */
	public IFuture startPlatform(String[] args);
	
	/**
	 *  Shutdown a platform.
	 *  @param cid The platform id.
	 */
	public IFuture shutdownPlatform(IComponentIdentifier cid);
}
