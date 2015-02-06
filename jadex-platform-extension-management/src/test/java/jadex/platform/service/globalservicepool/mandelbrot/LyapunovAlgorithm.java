package jadex.platform.service.globalservicepool.mandelbrot;

/**
 *  Algorithm for calculating Lyapunov fractals.
 */
public class LyapunovAlgorithm implements IFractalAlgorithm
{
	//-------- constants --------
	
	/** Generator string (any combination of As and Bs). */
	public static final String	GENERATOR	= "AAAAABBBBB";
	
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
		// Calculates population: x'=rx(1-x)
		// x: population relative to max. possible population (0-1)
		// Start population can be chosen arbitrarily, because convergence does not depend on initial value if max is large enough.
		// r: growth rate (also "fecundity factor")
		
		double	sum	= 0;
		double	x	= 0.5; // Start population.
		for(int i=1; i<max; i++)
		{
			double	r	= GENERATOR.charAt(i%GENERATOR.length())=='A' ? a : b;
			x	= r*x*(1-x);
			double	val	= r*(1-2*x);
			sum	+= Math.log(Math.abs(val));
		}
		
		double	lambda	= sum / max;	// Lyapunov exponent: >0 means chaos, <0 means convergence. From -infinity to +1(?)
		short	val	= sum>0 ? -1 : (short)(-Math.tanh(lambda)*(max-1));
//		System.out.println("lambda = "+lambda);
//		System.out.println("sum = "+sum);
//		System.out.println("val = "+val);
		return val;
	}
	
	/**
	 *  Get default settings for rendering the fractal. 
	 */
	public AreaData	getDefaultSettings()
	{
		return new AreaData(1.7, 4, 2.7, 4.2, 230, 150, (short)160, 10, 300, this, null);
	}

	/**
	 *  Should a cyclic color scheme be used?
	 */
	public boolean	useColorCycle()
	{
		return false;
	}

	/**
	 *  Can areas be filled?
	 */
	public boolean	isOptimizationAllowed()
	{
		return false;
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
