package jadex.micro.examples.mandelbrot;

/**
 *  Algorithm for calculating Lyapunov fractals.
 */
public class LyapunovAlgorithm implements IFractalAlgorithm
{
	//-------- constants --------
	
	/** Generator string (any combination of As and Bs). */
	public static String	GENERATOR	= "AB";
	
	//-------- IFractalAlgorithm interface --------
	
	/**
	 *  Determine the color of a point.
	 *  @param x	The x coordinate.
	 *  @param y	The y coordinate.
	 *  @param max	The maximum depth.
	 *  @return	A value for the point from 0 to max-1 or -1 for max.
	 */
	public short	determineColor(double a, double b, short max)
	{
		double	sum	= 0;
		double	x[]	= new double[max];
		x[0]	= 0.5;
		double	r0	= a;//???
		x[1]	= r0*x[0]*(1-x[0]);
		for(int n=1; n<max; n++)
		{
			double	rn	= GENERATOR.charAt((n-1)%GENERATOR.length())=='A' ? a : b;
			if(n<max-1)
				x[n+1]	= rn*x[n]*(1-x[n]);
			double	val	= rn*(1-2*x[n]);
			sum	+= Math.log(Math.abs(val));
		}
		double	lambda	= sum / max;	// assume -1 to 1 ???
//		System.out.println("lambda = "+lambda);
		
		return lambda<-1 ? 0 : (short)Math.abs(max-1-(lambda+1)*max/2);
	}
	
	//-------- singleton semantics --------
	
	/**
	 *  Get a string representation.
	 */
	public String toString()
	{
		return "Lyapunov";
	}
	
	/**
	 *  Test if two objects are equal.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof LyapunovAlgorithm;
	}
	
	/**
	 *  Get the hash code.
	 */
	public int hashCode()
	{
		return 31 + getClass().hashCode();
	}
}
