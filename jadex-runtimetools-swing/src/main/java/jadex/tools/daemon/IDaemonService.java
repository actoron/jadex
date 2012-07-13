package jadex.tools.daemon;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.IFuture;

import java.util.Set;

/**
 * 
 */
public interface IDaemonService //extends IService
{
	public static String ADDED = "added";

	public static String REMOVED = "removed";
	
	/**
	 *  Start a platform using a configuration.
	 *  @param args The arguments.
	 */
	public IFuture<IComponentIdentifier> startPlatform(StartOptions options);
	
	/**
	 *  Shutdown a platform.
	 *  @param cid The platform id.
	 */
	public IFuture<Void> shutdownPlatform(IComponentIdentifier cid);
	
	/**
	 *  Get the component identifiers of all (managed) platforms.
	 *  @return Collection of platform ids.
	 */
	public IFuture<Set<IComponentIdentifier>> getPlatforms();
	
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	public IFuture<Void> addChangeListener(final IRemoteChangeListener<IComponentIdentifier> listener);
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public IFuture<Void> removeChangeListener(final IRemoteChangeListener<IComponentIdentifier> listener);
}
