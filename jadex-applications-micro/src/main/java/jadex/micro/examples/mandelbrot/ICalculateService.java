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
	public IFuture calculateArea(double x1, double y1, double x2, double y2, double stepx, double stepy);
}
