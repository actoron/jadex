package jadex.micro.examples.mandelbrot;

import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;

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
	@Timeout(30000)
	public IFuture<AreaData> calculateArea(AreaData data);
}
