package jadex.adapter.base.envsupport.environment;

import jadex.commons.SimplePropertyChangeSupport;

import java.beans.PropertyChangeListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic IPropertyHolder implementation.
 */
public abstract class PropertyHolder implements IPropertyHolder
{
	/** The monitor. */
	protected Object monitor;
	
	/** The properties */
	protected Map properties;
	
	/** The property change support. */
	protected SimplePropertyChangeSupport pcs;
	
	/**
	 * Initializes the PropertyHolder, should be called by subclasses.
	 * 
	 */
	public PropertyHolder()
	{
		monitor = this;
		properties = new HashMap();
		this.pcs = new SimplePropertyChangeSupport(this);
	}
	
	/**
	 * Initializes the PropertyHolder, should be called by subclasses.
	 * @param monitor the monitor 
	 */
	public PropertyHolder(Object monitor)
	{
		this.monitor = monitor;
		properties = new HashMap();
		this.pcs = new SimplePropertyChangeSupport(this);
	}
	
	/**
	 * Returns a property.
	 * @param name name of the property
	 * @return the property
	 */
	public Object getProperty(String name)
	{
		synchronized(monitor)
		{
			return properties.get(name);
		}
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
		Object oldval;
		synchronized(monitor)
		{
			oldval = properties.get(name);
			properties.put(name, value);
		}
		pcs.firePropertyChange(name, oldval, value);
	}
	
	/** 
	 * Returns the monitor.
	 * @return the monitor
	 */
	public Object getMonitor()
	{
		return monitor;
	}
	
	/**
	 * Sets the monitor.
	 * 
	 * @param monitor the monitor
	 */
	protected void setMonitor(Object monitor)
	{
		this.monitor = monitor;
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
