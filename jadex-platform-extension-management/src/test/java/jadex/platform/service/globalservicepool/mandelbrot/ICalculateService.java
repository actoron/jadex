package jadex.platform.service.globalservicepool.mandelbrot;

import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Interface for calculating an area of points.
 */
public interface ICalculateService
{
	/**
	 *  Calculate colors for an area of points.
	 *  @param data	The area to be calculated.
	 *  @return	A future containing the calculated area.
	 */
//	@Timeout(30000)
	public ISubscriptionIntermediateFuture<CalculateEvent> calculateArea(AreaData data);
}
