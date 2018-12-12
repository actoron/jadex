package jadex.bdi.examples.cleanerworld_classic;

import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.beans.PropertyChangeListener;


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

	/** The property change support. */
	protected SimplePropertyChangeSupport pcs;
	
	//-------- constructors --------

	/**
	 *  Create a new LocationObject.
	 */
	public LocationObject()
	{
		// Empty constructor required for JavaBeans (do not remove).
		pcs = new SimplePropertyChangeSupport(this);
	}

	/**
	 *  Create a new LocationObject.
	 */
	public LocationObject(String id, Location location)
	{
		pcs = new SimplePropertyChangeSupport(this);
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
		String oldid = this.id;
		this.id = id;
		pcs.firePropertyChange("id", oldid, id);
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
		Location oldloc = this.location;
		this.location = location;
		pcs.firePropertyChange("location", oldloc, location);
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
		return o instanceof LocationObject && ((LocationObject)o).id.equals(id)
			&& o.getClass().equals(this.getClass());
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
			if(getLocation()!=null)
				clone.setLocation((Location)getLocation().clone());
			return clone;
		}
		catch(CloneNotSupportedException e)
		{
			throw new RuntimeException("Clone not supported");
		}
	}
	
	//-------- property methods --------

	/**
	 * Add a PropertyChangeListener to the listener list.
	 * The listener is registered for all properties.
	 *
	 * @param listener The PropertyChangeListener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Remove a PropertyChangeListener from the listener list.
	 * This removes a PropertyChangeListener that was registered
	 * for all properties.
	 *
	 * @param listener The PropertyChangeListener to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
	}
}
