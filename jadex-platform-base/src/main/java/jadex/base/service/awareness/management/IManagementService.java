package jadex.base.service.awareness.management;

import jadex.base.service.awareness.AwarenessInfo;
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
	public IFuture addAwarenessInfo(AwarenessInfo info);
	
	// todo:
//	/**
//	 *  Get the current awareness infos.
//	 *  @return The awareness infos.
//	 */
//	public IFuture getAwarenessInfos();
}
