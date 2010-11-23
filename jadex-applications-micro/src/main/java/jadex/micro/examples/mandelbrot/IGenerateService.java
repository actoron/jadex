package jadex.micro.examples.mandelbrot;

/**
 *  Service for generating a specific area.
 */
public interface IGenerateService
{
	/**
	 * 
	 */
	public void generateArea(double x1, double y1, double x2, double y2, int sizex, int sizey);
}
