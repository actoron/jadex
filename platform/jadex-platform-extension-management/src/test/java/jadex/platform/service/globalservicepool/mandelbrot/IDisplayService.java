package jadex.platform.service.globalservicepool.mandelbrot;

import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Service for displaying the result of a calculation. 
 */
public interface IDisplayService
{
	/**
	 *  Display the result of a calculation.
	 */
	public IFuture<Void> displayResult(AreaData result);

	/**
	 *  Display intermediate calculation results.
	 */
	public IFuture<Void> displayProgress(ProgressData progress);
	
	/**
	 *  Display intermediate calculation results.
	 */
	public IFuture<Void> displayPartialResult(AreaData all, AreaData data);
	
	/**
	 *  Subscribe to display events.
	 */
//	@Timeout(Timeout.NONE)
	public ISubscriptionIntermediateFuture<Object> subscribeToDisplayUpdates(String displayid);
}
