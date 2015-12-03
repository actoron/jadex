package jadex.bridge.service.types.daemon;

import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IDaemonService //extends IService
{
	/** Event for an added platform. */
	public static String ADDED = "added";

	/** Event for a removed platform. */
	public static String REMOVED = "removed";
	
	/**
	 *  Start a platform using a configuration.
	 *  Performs no checking if the new platform runs.
	 *  @param options The start arguments.
	 */
	public IFuture<Void> startPlatform(StartOptions options);
	
	/**
	 *  Start a platform using a configuration.
	 *  Wait for some time to check if the platform doesn't fail.
	 *  Only detects, if the new platform process exits during that time.
	 *  @param options The start arguments.
	 */
	public IFuture<Void> startPlatform(StartOptions options, long wait);
	
	/**
	 *  Start a platform using a configuration.
	 *  Wait for successful handshake and return the component identifier of the new platform.
	 *  Successful handshake means that the init phase of the new platform, including starting
	 *  of initial components, has completed successfully.
	 *  @param options The start arguments.
	 */
	public IFuture<IComponentIdentifier> startPlatformAndWait(StartOptions options);
	
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
