package jadex.commons;

import java.util.Set;

/** Interface for property-holding objects.
 */
public interface IPropertyObject
{
	/**
	 * Returns a property.
	 * @param name name of the property
	 * @return the property
	 */
	public Object getProperty(String name);
	
	/**
	 * Returns all of the properties.
	 * @return the properties
	 */
//	public Map getProperties();
	
	/**
	 * Returns all of the properties.
	 * @return the properties
	 */
	public Set getPropertyNames();
	
	/**
	 * Sets a property
	 * @param name name of the property
	 * @param value value of the property
	 */
	public void setProperty(String name, Object value);
	
	//-------- property methods --------

	/**
     *  Add a PropertyChangeListener to the listener list.
     *  The listener is registered for all properties.
     *  @param listener  The PropertyChangeListener to be added.
     */
//    public void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     *  Remove a PropertyChangeListener from the listener list.
     *  This removes a PropertyChangeListener that was registered
     *  for all properties.
     *  @param listener  The PropertyChangeListener to be removed.
     */
//    public void removePropertyChangeListener(PropertyChangeListener listener);
}
