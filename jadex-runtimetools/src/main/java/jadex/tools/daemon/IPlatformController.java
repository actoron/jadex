package jadex.tools.daemon;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.IFuture;

public interface IPlatformController {
	
	/**
	 *  Start a platform using a configuration.
	 *  @param args The arguments.
	 */
	IFuture startPlatform(StartOptions opt);
	
	/**
	 *  Shutdown a platform.
	 *  @param cid The platform id.
	 */
	IFuture shutdownPlatform(final IComponentIdentifier cid);
	
	/**
	 *  Get the component identifiers of all (managed) platforms.
	 *  @return Collection of platform ids.
	 */
	IFuture getPlatforms();
	
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	void addChangeListener(IRemoteChangeListener listener);
	
	/**
	 * Remove a change listener.
	 * 
	 * @param listener
	 *            The change listener.
	 */
	void removeChangeListener(IRemoteChangeListener listener);
}
