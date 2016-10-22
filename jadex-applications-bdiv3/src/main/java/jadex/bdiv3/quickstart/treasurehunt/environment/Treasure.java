package jadex.bdiv3.quickstart.treasurehunt.environment;

import java.awt.Point;
import java.util.Random;

/**
 *  A treasure object.
 */
public class Treasure	implements Cloneable
{
	//-------- static part --------
	
	/** The id counter: */
	protected static int	IDCNT	= 0;
	
	/**
	 *  Create a random treasure.
	 *  @param rnd	The random number generator.
	 *  @param width	The environment width.
	 *  @param height	The environment height.
	 */
	protected static synchronized Treasure	create(Random rnd, int width, int height)
	{
		Treasure	t	= new Treasure();
		t.id	= ++IDCNT;
		t.location	= new Point(rnd.nextInt(width), rnd.nextInt(height));
		t.weight	= rnd.nextInt(10)+1;
		return t;
	}

	//-------- attributes --------
	
	/** The id. */
	protected int id;
	
	/** The location. */
	protected Point	location;
	
	/** The weight. */
	protected int weight;

	//-------- methods --------
	
	/**
	 *  Calculate the hash code.
	 */
	@Override
	public int hashCode()
	{
		return 31+id;
	}
	
	/**
	 *  Test if two treasures are the same.
	 */
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Treasure && id==((Treasure)obj).id;
	}
	
	/**
	 *  Create a copy of this treasure.
	 */
	@Override
	public Treasure clone()
	{
		try
		{
			return (Treasure)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			// Shouldn't happen.
			throw new RuntimeException(e);
		}
	}
}
