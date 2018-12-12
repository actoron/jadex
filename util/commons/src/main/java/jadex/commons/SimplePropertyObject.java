package jadex.commons;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import jadex.commons.beans.PropertyChangeListener;

/**
 * Basic IPropertyObject implementation.
 */
public class SimplePropertyObject implements IPropertyObject
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
	public Set getPropertyNames()
	{
		return properties==null? Collections.EMPTY_SET: properties.keySet();
	}
	
	/**
	 * Sets a property
	 * @param name name of the property
	 * @param value value of the property
	 */
	public void setProperty(String name, Object value)
	{
		if(properties==null)
			properties = new LinkedHashMap(); // preserve order for EIS :-( parameters
		Object oldval = properties.get(name);
		properties.put(name, value);
		if(pcs!=null)
			pcs.firePropertyChange(name, oldval, value);
	}
	
	/*
	 * (non-Javadoc)
	 * @see jadex.commons.IPropertyObject#hasProperty(java.lang.String)
	 */
	public boolean hasProperty(String name) {
		return properties != null && properties.containsKey(name);
	}
	
	//-------- bean accessors --------
	
	/**
	 *  Get the properties (bean accessor).
	 */
	public Map	getProperties()
	{
		return this.properties;
	}

	/**
	 *  Set the properties (bean accessor).
	 */
	public void	setProperties(Map properties)
	{
		this.properties	= properties;
	}

	//-------- property methods --------

	/**
     *  Add a PropertyChangeListener to the listener list.
     *  The listener is registered for all properties.
     *  @param listener  The PropertyChangeListener to be added.
     */
    public void addPropertyChangeListener(PropertyChangeListener listener)
	{
    	if(pcs==null)
    		this.pcs = new SimplePropertyChangeSupport(this);
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
    	if(pcs!=null)
    		pcs.removePropertyChangeListener(listener);
    }

    /**
     *  Get the string representation.  
     */
	public String toString()
	{
		return SReflect.getInnerClassName(this.getClass());
	}
    
    
}
