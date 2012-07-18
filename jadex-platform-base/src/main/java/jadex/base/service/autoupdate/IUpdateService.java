package jadex.base.service.autoupdate;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IUpdateService
{
	/**
	 * 
	 */
	public IFuture<Void> performUpdate();

	/**
	 * 
	 */
	public IFuture<Void> acknowledgeUpdate();
}
