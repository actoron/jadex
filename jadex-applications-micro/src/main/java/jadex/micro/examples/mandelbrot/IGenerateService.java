package jadex.micro.examples.mandelbrot;

import jadex.commons.IFuture;

/**
 *  Service for generating a specific area.
 */
public interface IGenerateService
{
	/**
	 *  Generate a specific area using a defined x and y size.
	 */
	public IFuture generateArea(double x1, double y1, double x2, double y2, int sizex, int sizey, int max);
}
