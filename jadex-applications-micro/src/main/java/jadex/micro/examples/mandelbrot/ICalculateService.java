package jadex.micro.examples.mandelbrot;

import jadex.commons.IFuture;
import jadex.commons.service.IService;

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
