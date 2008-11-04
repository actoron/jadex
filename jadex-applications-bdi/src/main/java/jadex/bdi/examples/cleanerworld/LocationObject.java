package jadex.bdi.examples.cleanerworld;


/**
 *  Editable Java class for concept LocationObject of cleaner-generated ontology.
 */
public abstract class LocationObject implements Cloneable
{
	//-------- attributes ----------

	/** Attribute for slot id. */
	protected String id;

	/** The location of the object. */
	protected Location location;

	//-------- constructors --------

	/**
	 *  Create a new LocationObject.
	 */
	public LocationObject()
	{
		// Empty constructor required for JavaBeans (do not remove).
	}

	/**
	 *  Create a new LocationObject.
	 */
	public LocationObject(String id, Location location)
	{
		setId(id);
		setLocation(location);
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

	//-------- object methods --------

	/**
	 *  Get a string representation of this LocationObject.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "LocationObject(" + "id=" + getId() + ", location=" + getLocation() + ")";
	}

	//-------- custom code --------

	/**
	 *  Test if two instances are equal.
	 *  @return True, if equal.
	 */
	public boolean equals(Object o)
	{
		return o instanceof LocationObject && ((LocationObject)o).id.equals(id);
	}

	/**
	 *  Get the hashcode for this object.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		return id.hashCode();
	}

	/**
	 *  Clone the object.
	 */
	public Object clone()
	{
		try
		{
			LocationObject clone = (LocationObject)super.clone();
			clone.setLocation((Location)getLocation().clone());
			return clone;
		}
		catch(CloneNotSupportedException e)
		{
			assert false;
			throw new RuntimeException("Clone not supported");
		}
	}
}
