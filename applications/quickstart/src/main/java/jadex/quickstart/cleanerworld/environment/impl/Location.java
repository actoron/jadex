package jadex.quickstart.cleanerworld.environment.impl;

import jadex.quickstart.cleanerworld.environment.ILocation;

/**
 *  Editable Java class for concept Location of cleaner-generated ontology.
 */
public class Location implements ILocation, Cloneable
{
	//-------- constants --------

	/** Distance, when two locations are considered near. */
	public static final double DEFAULT_TOLERANCE = 0.001;

	//-------- attributes ----------

	/** The x-coordinate. */
	private double x;

	/** The y-coordinate. */
	private double y;

	//-------- constructors --------

	/**
	 *  Create a new Location.
	 */
	public Location()
	{
		// Empty constructor required for JavaBeans (do not remove).
	}

	/**
	 *  Create a new Location.
	 */
	public Location(double x, double y)
	{
		setX(x);
		setY(y);
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

	//-------- object methods --------

	/**
	 *  Get a string representation of this Location.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Location(" + "x=" + getX() + ", y=" + getY() + ")";
	}

	//-------- custom code --------

	/**
	 *  Caculate is a location is near this location.
	 *  @return The distance.
	 */
	public double getDistance(ILocation other)
	{
		assert other != null;
		return Math.sqrt((other.getY() - this.y) * (other.getY() - this.y) + (other.getX() - this.x) * (other.getX() - this.x));
	}

	/**
	 *  Check, if two locations are near to each other
	 *  using the default tolerance.
	 *  @return True, if two locations are near to each other.
	 */
	public boolean isNear(ILocation other)
	{
		return isNear(other, DEFAULT_TOLERANCE);
	}

	/**
	 *  Check, if two locations are near to each other.
	 *  @param tolerance	The distance, when two locations are considered near.
	 *  @return True, if two locations are near to each other.
	 */
	public boolean isNear(ILocation other, double tolerance)
	{
		return getDistance(other) <= tolerance;
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
		return ret;
	}
	
	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return (int)(x*21+y);
	}

	/**
	 *  Clone the object.
	 */
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			assert false;
			throw new RuntimeException("Clone not supported");
		}
	}
}
