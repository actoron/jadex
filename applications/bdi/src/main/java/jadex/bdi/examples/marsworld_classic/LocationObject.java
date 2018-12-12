package jadex.bdi.examples.marsworld_classic;

import java.io.Serializable;

import jadex.commons.SReflect;


/**
 *  An object with a location.
 */
public class LocationObject implements Serializable
{
	//-------- attributes ----------

	/** Attribute for slot id. */
	protected String	id;

	/** The location of the object. */
	protected Location	location;

	//-------- constructors --------

	/**
	 *  Create a new location object.
	 *  Empty bean contructor.
	 */
	public LocationObject()
	{
	}

	/**
	 *  Create a new location object.
	 *  @param id	The id.
	 *  @param location	The location.
	 */
	public LocationObject(String id, Location location)
	{
		this.id = id;
		this.location = location;
	}

	//-------- accessor methods --------

	/**
	 *  Get the id of this LocationObject.
	 * @return id
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 *  Set the id of this LocationObject.
	 * @param id the value to be set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 *  Get the location of this LocationObject.
	 *  The location of the object.
	 * @return location
	 */
	public Location getLocation()
	{
		return this.location;
	}

	/**
	 *  Set the location of this LocationObject.
	 *  The location of the object.
	 * @param location the value to be set
	 */
	public void setLocation(Location location)
	{
		this.location = location;
	}

	//-------- methods --------

	/**
	 *  Test if two instances are equal.
	 *  @return True, if equal.
	 */
	public boolean equals(Object o)
	{
		return o instanceof LocationObject && ((LocationObject)o).id.equals(id);
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return 31 + id.hashCode();
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return SReflect.getInnerClassName(this.getClass()) + " loc: " + location;
	}

}
