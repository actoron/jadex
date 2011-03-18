package jadex.micro.examples.mandelbrot;

import jadex.bridge.service.IService;
import jadex.commons.future.IFuture;

/**
 *  Interface for calculating an area of points.
 */
public interface ICalculateService	extends IService
{
	/**
	 *  Calculate colors for an area of points.
	 */
	public IFuture calculateArea(AreaData data);
}
