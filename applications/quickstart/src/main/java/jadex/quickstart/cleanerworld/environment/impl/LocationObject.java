package jadex.quickstart.cleanerworld.environment.impl;

import jadex.commons.SimplePropertyChangeSupport;
import jadex.commons.beans.PropertyChangeListener;
import jadex.quickstart.cleanerworld.environment.ILocationObject;


/**
 *  Base class for all map objects.
 */
public abstract class LocationObject implements ILocationObject, Cloneable
{
	//-------- attributes ----------

	/** Unique id. */
	private String id;

	/** The location of the object. */
	private Location location;

	/** The property change support. */
	SimplePropertyChangeSupport pcs;
	
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
		this();
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
		return 31 + id.hashCode();
	}
	
	/**
	 *  Update this cleaner.
	 */
	public void update(LocationObject obj)
	{
		assert this.getId().equals(obj.getId());
		setLocation(obj.getLocation());
	}

	/**
	 *  Clone the object.
	 */
	public LocationObject clone()
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
	
	/**
	 *  Get the property change handler for firing events.
	 */
	protected SimplePropertyChangeSupport	getPropertyChangeHandler()
	{
		return pcs;
	}
}
