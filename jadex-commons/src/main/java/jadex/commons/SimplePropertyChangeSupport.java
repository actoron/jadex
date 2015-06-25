package jadex.commons;

import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This class offers part of the functionality of the original
 * Java-PropertyChangeSupport class. It had to be rewritten in order to support
 * the bytecode enhancements of javaflow
 */
public class SimplePropertyChangeSupport	implements Serializable
{
	private List<PropertyChangeListener>	listeners;

	private Object		source;

	public SimplePropertyChangeSupport(Object sourceBean)
	{
		if(sourceBean == null)
		{
			throw new NullPointerException();
		}
		listeners = new ArrayList<PropertyChangeListener>();
		source = sourceBean;
	}

	/**
	 * Add a PropertyChangeListener to the listener list. The listener is
	 * registered for all properties.
	 * 
	 * @param listener The PropertyChangeListener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		if(this.listeners.contains(listener))
		{
			System.out.println("dreck");
		}
		
		this.listeners.add(listener);
	}

	/**
	 * Remove a PropertyChangeListener from the listener list. This removes a
	 * PropertyChangeListener that was registered for all properties.
	 * 
	 * @param listener The PropertyChangeListener to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		this.listeners.remove(listener);
	}

	/**
	 * Report a bound property update to any registered listeners. No event is
	 * fired if old and new are equal and non-null.
	 * 
	 * @param propertyName The programmatic name of the property that was
	 *        changed.
	 * @param oldValue The old value of the property.
	 * @param newValue The new value of the property.
	 */
	public void firePropertyChange(String propertyName, Object oldValue,
			Object newValue)
	{
		if(oldValue != null && newValue != null && oldValue.equals(newValue))
		{
			return;
		}

		PropertyChangeEvent evt = new PropertyChangeEvent(source, propertyName,
				oldValue, newValue);

		for(int i = 0; i < listeners.size(); i++)
		{
			PropertyChangeListener oneListener = (PropertyChangeListener)listeners.get(i);
			oneListener.propertyChange(evt);
		}
	}

	/**
	 * Report an int bound property update to any registered listeners. No event
	 * is fired if old and new are equal and non-null.
	 * <p>
	 * This is merely a convenience wrapper around the more general
	 * firePropertyChange method that takes Object values.
	 * 
	 * @param propertyName The programmatic name of the property that was
	 *        changed.
	 * @param oldValue The old value of the property.
	 * @param newValue The new value of the property.
	 */
	public void firePropertyChange(String propertyName, int oldValue, int newValue)
	{
		if(oldValue == newValue)
		{
			return;
		}
		firePropertyChange(propertyName, Integer.valueOf(oldValue), Integer.valueOf(newValue));
	}

	/**
	 * Fire an existing PropertyChangeEvent to any registered listeners. No
	 * event is fired if the given event's old and new values are equal and
	 * non-null.
	 * 
	 * @param evt The PropertyChangeEvent object.
	 */
	public void firePropertyChange(PropertyChangeEvent evt)
	{
		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();
		if(oldValue != null && newValue != null && oldValue.equals(newValue))
		{
			return;
		}
		for(int i = 0; i < listeners.size(); i++)
		{
			PropertyChangeListener oneListener = (PropertyChangeListener)listeners
					.get(i);
			oneListener.propertyChange(evt);
		}
	}
}
