package jadex.adapter.base.envsupport.environment;

import jadex.commons.SimplePropertyObject;

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;


/**
 * Basic synchronized IPropertyObject implementation.
 */
public abstract class PropertyHolder extends SimplePropertyObject
{
	// -------- attributes --------

	/** The monitor. */
	protected Object	monitor;

	// -------- constructors --------

	/**
	 * Initializes the PropertyHolder, should be called by subclasses.
	 * /
	public PropertyHolder()
	{
		monitor = this;
	}

	/**
	 * Initializes the PropertyHolder, should be called by subclasses.
	 * 
	 * @param monitor the monitor
	 */
	public PropertyHolder(Object monitor)
	{
		this.monitor = monitor;
	}

	// -------- methods --------

	/**
	 * Returns a property.
	 * 
	 * @param name name of the property
	 * @return the property
	 */
	public Object getProperty(String name)
	{
		synchronized(monitor)
		{
			return super.getProperty(name);
		}
	}

	/**
	 * Returns all of the properties.
	 * 
	 * @return the properties
	 */
	public Map getProperties()
	{
		synchronized(monitor)
		{
			return super.getProperties();
		}
	}

	/**
	 * Sets a property
	 * 
	 * @param name name of the property
	 * @param value value of the property
	 */
	public void setProperty(String name, Object value)
	{
		// cannot call super.set because firePropChanged must be outside of
		// synchronized block
		Object oldval;
		synchronized(monitor)
		{
			if(properties == null)
				properties = new HashMap();
			oldval = properties.get(name);
			properties.put(name, value);
		}
		pcs.firePropertyChange(name, oldval, value);
	}

	/**
	 * Returns the monitor.
	 * 
	 * @return the monitor
	 */
	public Object getMonitor()
	{
		return monitor;
	}

	/**
	 * Sets the monitor.
	 * 
	 * @param monitor the monitor / protected void setMonitor(Object monitor) {
	 *        this.monitor = monitor; }
	 */

	// -------- property methods --------
	/**
	 * Add a PropertyChangeListener to the listener list. The listener is
	 * registered for all properties.
	 * 
	 * @param listener The PropertyChangeListener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		synchronized(monitor)
		{
			super.addPropertyChangeListener(listener);
		}
	}

	/**
	 * Remove a PropertyChangeListener from the listener list. This removes a
	 * PropertyChangeListener that was registered for all properties.
	 * 
	 * @param listener The PropertyChangeListener to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		synchronized(monitor)
		{
			super.removePropertyChangeListener(listener);
		}
	}
}
