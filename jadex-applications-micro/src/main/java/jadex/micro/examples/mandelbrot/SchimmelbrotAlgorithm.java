package jadex.micro.examples.mandelbrot;

/**
 *  Algorithm for calculating the mandelbrot set.
 */
public class SchimmelbrotAlgorithm implements IFractalAlgorithm
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
		double	val	= (xn*xn*13+yn*yn*17);
		return val>max? -1: (short)val;
	}
	
	//-------- singleton semantics --------
	
	/**
	 *  Get a string representation.
	 */
	public String toString()
	{
		return "Schimmelbrot";
	}
	
	/**
	 *  Test if two objects are equal.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof SchimmelbrotAlgorithm;
	}
	
	/**
	 *  Get the hash code.
	 */
	public int hashCode()
	{
		return 31 + getClass().hashCode();
	}
}
