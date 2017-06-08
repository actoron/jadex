package jadex.bridge.service.types.awareness;

import java.util.Collection;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Service for managing discovery infos.
 */
//@Properties(@NameValue(name="system", value="true"))
@Service(system=true)
public interface IAwarenessManagementService
{
	/**
	 *  Announce an awareness info.
	 *  @param info The info (passed as local reference).
	 *  @return True, if was a new awareness info. 
	 */
	public IFuture<Boolean> addAwarenessInfo(@Reference(local=true, remote=false) AwarenessInfo info);
	
	/**
	 *  Get the discovery info for a platform, if any.
	 *  @param cid	The platform id.
	 *  @return The discovery info.
	 */
	public IFuture<DiscoveryInfo> getPlatformInfo(IComponentIdentifier cid);
	
	
	/**
	 *  Get the currently known platforms.
	 *  @return The discovery infos of known platforms.
	 */
	public IFuture<Collection<DiscoveryInfo>> getKnownPlatforms();
	
	/**
	 *  Retrieve information about platforms as they appear or vanish.
	 *  @param include_initial	If true, information about initially known platforms will be immediately posted to the caller.
	 *  	Otherwise only changes that happen after the subscription will be posted. 
	 *  @return An intermediate future that is notified about any changes.
	 */
	// Not necessary due to SFuture.getNoTimeoutFuture
	//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<DiscoveryInfo> subscribeToPlatformList(boolean include_initial);
	
	// Todo: create / remove proxies?
	
	// Todo: set includes / excludes?
}
