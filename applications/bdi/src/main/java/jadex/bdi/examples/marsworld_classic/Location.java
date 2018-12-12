package jadex.bdi.examples.marsworld_classic;

import java.io.Serializable;


/**
 * This class represents a location in the environment.
 */
public class Location implements Serializable
{
	//-------- constants --------

	/** Distance, when two locations are considered near. */
	public static final double	DEFAULT_TOLERANCE	= 0.001;

	//-------- attributes ----------

	/** The x-coordinate. */
	protected double	x;

	/** The y-coordinate. */
	protected double	y;

	//-------- constructors --------

	/**
	 *  Create a new location.
	 *  Empty bean constructor.
	 */
	public Location()
	{
	}

	/**
	 *  Create a new location.
	 *  @param x	The x coordinate.
	 *  @param y	The y coordinate.
	 */
	public Location(double x, double y)
	{
		this.x = x;
		this.y = y;
	}

	//-------- accessor methods --------

	/**
	 *  Get the x of this Location.
	 *  The x-coordinate.
	 * @return x
	 */
	public double getX()
	{
		return this.x;
	}

	/**
	 *  Set the x of this Location.
	 *  The x-coordinate.
	 * @param x the value to be set
	 */
	public void setX(double x)
	{
		this.x = x;
	}

	/**
	 *  Get the y of this Location.
	 *  The y-coordinate.
	 * @return y
	 */
	public double getY()
	{
		return this.y;
	}

	/**
	 *  Set the y of this Location.
	 *  The y-coordinate.
	 * @param y the value to be set
	 */
	public void setY(double y)
	{
		this.y = y;
	}

	//-------- methods --------

	/**
	 *  Caculate is a location is near this location.
	 *  @return The distance.
	 */
	public double getDistance(Location other)
	{
		return Math.sqrt((other.y - this.y) * (other.y - this.y) + (other.x - this.x) * (other.x - this.x));
	}

	/**
	 *  Check, if two locations are near to each other
	 *  using the default tolerance.
	 *  @return True, if two locations are near to each other.
	 */
	public boolean isNear(Location other)
	{
		return isNear(other, DEFAULT_TOLERANCE);
	}

	/**
	 *  Check, if two locations are near to each other.
	 *  @param tolerance	The distance, when two locations are considered near.
	 *  @return True, if two locations are near to each other.
	 */
	public boolean isNear(Location other, double tolerance)
	{
		return getDistance(other) <= tolerance;
	}

	/**
	 *  Convert the location to a string representation.
	 */
	public String toString()
	{
		return "(" + x + ", " + y + ")";
	}

	/**
	 *  Test if two instances are equal.
	 *  @return True, if equal.
	 */
	public boolean equals(Object o)
	{
		boolean ret = false;
		if(o instanceof Location)
		{
			Location loc = (Location)o;
			if(loc.x == x && loc.y == y)
				ret = true;
		}
		//System.out.println("equals: "+this+" --- "+o);
		return ret;
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return 31 + Double.valueOf(x).hashCode() + 31*Double.valueOf(y).hashCode();
	}
}