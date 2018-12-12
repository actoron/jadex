package jadex.micro.examples.mandelbrot;

/**
 *  Algorithm for calculating a fractal.
 */
public interface IFractalAlgorithm
{
	/**
	 *  Determine the color of a point.
	 *  @param x	The x coordinate.
	 *  @param y	The y coordinate.
	 *  @param max	The maximum depth.
	 *  @return	A value for the point from 0 to max-1
	 *    or -1 if the value is at the maximum.
	 */
	public short	determineColor(double x, double y, short max);


	/**
	 *  Can areas be filled?
	 */
	public boolean	isOptimizationAllowed();
	
	/**
	 *  Get default settings for rendering the fractal. 
	 */
	public AreaData	getDefaultSettings();

	/**
	 *  Should a cyclic color scheme be used?
	 */
	public boolean	useColorCycle();
}
