package jadex.tools.daemon;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.IChangeListener;
import jadex.commons.ICommand;
import jadex.commons.IFuture;

/**
 * 
 */
public interface IDaemonService
{
	public static String ADDED = "added";

	public static String REMOVED = "removed";
	
	/**
	 *  Start a platform using a configuration.
	 *  @param args The arguments.
	 */
	public IFuture startPlatform(StartOptions options);
	
	/**
	 *  Shutdown a platform.
	 *  @param cid The platform id.
	 */
	public IFuture shutdownPlatform(IComponentIdentifier cid);
	
	/**
	 *  Get the component identifiers of all (managed) platforms.
	 *  @return Collection of platform ids.
	 */
	public IFuture getPlatforms();
	
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	public void addChangeListener(final IChangeListener listener);
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public void removeChangeListener(final IChangeListener listener);
}
