package jadex.micro.examples.mandelbrot;

import jadex.commons.IFuture;

/**
 *  Interface for calculating an area of points.
 */
public interface ICalculateService
{
	/**
	 *  Calculate colors for an area of points.
	 */
	public IFuture calculateArea(AreaData data);
}
