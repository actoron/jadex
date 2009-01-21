package jadex.bdi.examples.hunterprey2;

import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;


/**
 *  Editable Java class for concept WorldObject of hunterprey ontology.
 */
public class WorldObject
{
	//-------- static attributes --
	
	public static IVector2 WORLD_OBJECT_SIZE = new Vector2Double(1);
	
	//-------- attributes ----------

	/** The location of the object. */
	protected Location location;
	
	/** The simulation Id of this object */
	protected Integer simId;

	//-------- constructors --------

	/**
	 *  Create a new WorldObject.
	 */
	public WorldObject()
	{
		// Empty constructor required for JavaBeans (do not remove).
	}

	/**
	 *  Create a new WorldObject.
	 */
	public WorldObject(Location location)
	{
		// Constructor using required slots (change if desired).
		setLocation(location);
	}

	//-------- accessor methods --------

	/**
	 *  Get the location of this WorldObject.
	 *  The location of the object.
	 * @return location
	 */
	public Location getLocation()
	{
		return this.location;
	}

	/**
	 *  Set the location of this WorldObject.
	 *  The location of the object.
	 * @param location the value to be set
	 */
	public void setLocation(Location location)
	{
		this.location = location;
	}

	/**
	 *  Get the simulation id of this WorldObject.
	 *  The location of the object.
	 * @return simId
	 */
	public Integer getSimId()
	{
		return this.simId;
	}

	/**
	 *  Set the simulation id of this WorldObject.
	 * @param simId the value to be set
	 */
	public void setSimId(Integer simId)
	{
		this.simId = simId;
	}
	
	//-------- custom code --------

	/**
	 *  Get a string representation of this WorldObject.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "WorldObject(" + "location=" + getLocation() + ")";
	}

	/**
	 *  Test if two worldobjects are equal.
	 */
	public boolean equals(Object o)
	{
		if (o == null)
			return false;
		
		return o.getClass() == this.getClass() && ((WorldObject)o).getLocation().equals(this.getLocation());
	}

	/**
	 *  Get the hash code of the world object.
	 */
	public int hashCode()
	{
		return getClass().hashCode() ^ getLocation().hashCode();
	}
	
	/**
	 * All WorldObjects has to implement the clone() method
	 */
	public Object clone()
	{
		WorldObject ret = null;
		try
		{
			ret = (WorldObject)getClass().newInstance();
			ret.setLocation(this.getLocation());
			ret.setSimId(this.getSimId());
		}
		catch(InstantiationException e)
		{
			e.printStackTrace();
		}
		catch(IllegalAccessException e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
}
