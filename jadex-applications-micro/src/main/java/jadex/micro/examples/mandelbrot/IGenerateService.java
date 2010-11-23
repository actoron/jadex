package jadex.micro.examples.mandelbrot;

import jadex.commons.IFuture;

/**
 *  Service for generating a specific area.
 */
public interface IGenerateService
{
	/**
	 * 
	 */
	public IFuture generateArea(double x1, double y1, double x2, double y2, int sizex, int sizey);
}
