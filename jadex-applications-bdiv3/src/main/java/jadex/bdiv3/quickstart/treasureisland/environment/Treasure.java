package jadex.bdiv3.quickstart.treasureisland.environment;

import java.awt.Point;
import java.util.Random;

/**
 *  A treasure object.
 */
public class Treasure	implements Cloneable
{
	/**
	 *  Create a random treasure.
	 *  @param rnd	The random number generator.
	 *  @param width	The environment width.
	 *  @param height	The environment height.
	 */
	protected static Treasure	create(Random rnd, int width, int height)
	{
		Treasure	t	= new Treasure();
		t.location	= new Point(rnd.nextInt(width), rnd.nextInt(height));
		t.weight	= rnd.nextInt(10)+1;
		return t;
	}

	//-------- attributes --------
	
	/** The location. */
	protected Point	location;
	
	/** The weight. */
	protected int weight;

	//-------- methods --------

	/**
	 *  Get the location of the treasure.
	 *  @return	The location
	 */
	public Point	getLocation()
	{
		// Return copy to prevent manipulation of original location from agent. 
		return new Point(location.x, location.y);
	}
}
