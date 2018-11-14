package jadex.extension.envsupport.environment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.meta.IPropertyMetaDataSet;
import jadex.commons.meta.TypedPropertyObject;


/**
 * Basic synchronized IPropertyObject implementation.
 */
public abstract class SynchronizedPropertyObject extends TypedPropertyObject
{
	// -------- attributes --------

	/** The monitor. */
	protected Object	monitor;

	// -------- constructors --------

	/**
	 * Initializes the PropertyHolder, should be called by subclasses.
	 * 
	 * @param monitor the monitor
	 */
	public SynchronizedPropertyObject(IPropertyMetaDataSet propertiesMeta, Object monitor)
	{
		super(propertiesMeta);
		
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
	 * /
	public Map getProperties()
	{
		synchronized(monitor)
		{
			return super.getProperties();
		}
	}*/
	
	/**
	 * Returns all of the properties.
	 * @return the properties
	 */
	public Set getPropertyNames()
	{
		synchronized(monitor)
		{
			return properties==null? Collections.EMPTY_SET: properties.keySet();
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
			oldval = properties.put(name, value);
		}
		if(pcs!=null)
			pcs.firePropertyChange(name, oldval, value);
	}
	
	/**
	 *  Test if has a property.
	 */
	public boolean hasProperty(String name) 
	{
		synchronized (monitor) 
		{
			return super.hasProperty(name);
		}
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
