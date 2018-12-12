package jadex.platform.service.autoupdate;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

/**
 *  Update service interface.
 */
public interface IUpdateService
{
	/**
	 *  Perform an update with the update info data.
	 *  @param ui The update info.
	 */
	public IFuture<IComponentIdentifier> performUpdate(UpdateInfo ui);

//	/**
//	 * 
//	 */
//	public IFuture<Void> acknowledgeUpdate();
}
