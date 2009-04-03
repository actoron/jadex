package jadex.commons;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic IPropertyObject implementation.
 */
public abstract class SimplePropertyObject implements IPropertyObject
{
	//-------- attributes --------
	
	/** The properties */
	protected Map properties;
	
	/** The property change support. */
	protected SimplePropertyChangeSupport pcs;
	
	//-------- constructors --------
	
	/**
	 * Create a new property object.
	 */
	public SimplePropertyObject()
	{
		this.pcs = new SimplePropertyChangeSupport(this);
	}
	
	//-------- methods --------
	
	/**
	 * Returns a property.
	 * @param name name of the property
	 * @return the property
	 */
	public Object getProperty(String name)
	{
		return properties==null? null: properties.get(name);
	}
	
	/**
	 * Returns all of the properties.
	 * @return the properties
	 */
	public Map getProperties()
	{
		return properties==null? Collections.EMPTY_MAP: properties;
	}
	
	/**
	 * Sets a property
	 * @param name name of the property
	 * @param value value of the property
	 */
	public void setProperty(String name, Object value)
	{
		if(properties==null)
			properties = new HashMap();
		Object oldval = properties.get(name);
		properties.put(name, value);
		pcs.firePropertyChange(name, oldval, value);
	}
	
	//-------- property methods --------

	/**
     *  Add a PropertyChangeListener to the listener list.
     *  The listener is registered for all properties.
     *  @param listener  The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.addPropertyChangeListener(listener);
    }

    /**
     *  Remove a PropertyChangeListener from the listener list.
     *  This removes a PropertyChangeListener that was registered
     *  for all properties.
     *  @param listener  The PropertyChangeListener to be removed.
     */
    public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		pcs.removePropertyChangeListener(listener);
    }
}
