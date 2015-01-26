package jadex.platform.service.globalservicepool.mandelbrot;

/**
 *  Algorithm for calculating the mandelbrot set.
 */
public class MandelbrotAlgorithm implements IFractalAlgorithm
{
	//-------- IFractalAlgorithm interface --------
	
	/**
	 *  Determine the color of a point.
	 *  @param x	The x coordinate.
	 *  @param y	The y coordinate.
	 *  @param max	The maximum depth.
	 *  @return	A value for the point from 0 to max.
	 */
	public short	determineColor(double xn, double yn, short max)
	{
		double x0 = xn;
		double y0 = yn;
		short i = 0;
		double c =  Math.sqrt(xn*xn + yn*yn);
		
		for(i=0; c<2 && i<max; i++)
		{
			double xn1 = xn*xn - yn*yn + x0;
			double yn1 = 2*xn*yn + y0;
			xn = xn1;
			yn = yn1;
			c =  Math.sqrt(xn*xn + yn*yn);
		}
		
		return i==max? -1: i;
	}
	

	/**
	 *  Can areas be filled?
	 */
	public boolean	isOptimizationAllowed()
	{
		return true;
	}

	
	/**
	 *  Get default settings for rendering the fractal. 
	 */
	public AreaData	getDefaultSettings()
	{
		return new AreaData(-2, 1, -1.5, 1.5, 100, 100, (short)256, 10, 300, this, null);
	}
	
	/**
	 *  Should a cyclic color scheme be used?
	 */
	public boolean	useColorCycle()
	{
		return true;
	}
	
	//-------- singleton semantics --------
	
	/**
	 *  Get a string representation.
	 */
	public String toString()
	{
		return "Mandelbrot";
	}
	
	/**
	 *  Test if two objects are equal.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof MandelbrotAlgorithm;
	}
	
	/**
	 *  Get the hash code.
	 */
	public int hashCode()
	{
		return 31 + getClass().hashCode();
	}
}
