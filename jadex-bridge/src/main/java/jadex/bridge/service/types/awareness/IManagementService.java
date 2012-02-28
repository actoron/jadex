package jadex.bridge.service.types.awareness;

import jadex.commons.IChangeListener;
import jadex.commons.future.IFuture;

/**
 *  Service for managing discovery infos.
 */
public interface IManagementService
{
	/**
	 *  Announce an awareness info.
	 *  @param info The info.
	 *  @return True, if was new awareness info. 
	 */
	public IFuture<Boolean> addAwarenessInfo(AwarenessInfo info);
	
	// todo:
//	/**
//	 *  Get the current awareness infos.
//	 *  @return The awareness infos.
//	 */
//	public IFuture getAwarenessInfos();
	
//	/**
//	 *  Add a change listener.
//	 *  @param listener The change listener.
//	 */
//	public IFuture<Void> addChangeListener(IChangeListener<AwarenessInfo> listener);
//	
//	/**
//	 *  Remove a change listener.
//	 *  @param listener The change listener.
//	 */
//	public IFuture<Void> removeChangeListener(IChangeListener<AwarenessInfo> listener);
	
}
