package jadex.platform.service.autoupdate;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IUpdateService
{
	/**
	 * 
	 */
	public IFuture<Void> performUpdate(UpdateInfo ui);

//	/**
//	 * 
//	 */
//	public IFuture<Void> acknowledgeUpdate();
}
