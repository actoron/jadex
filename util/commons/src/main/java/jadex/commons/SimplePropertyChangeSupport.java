package jadex.commons;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.beans.PropertyChangeListener;


/**
 * This class offers part of the functionality of the original
 * Java-PropertyChangeSupport class. It had to be rewritten in order to support
 * the bytecode enhancements of javaflow
 */
public class SimplePropertyChangeSupport	implements Serializable
{
	private List<PropertyChangeListener>	listeners;

	private Object		source;
	
//	private Object comp;

	public SimplePropertyChangeSupport(Object sourceBean)
	{
		if(sourceBean == null)
		{
			throw new NullPointerException();
		}
		listeners = Collections.synchronizedList(new ArrayList<PropertyChangeListener>());
		source = sourceBean;
//		comp	= getComp();
	}

	/**
	 * Add a PropertyChangeListener to the listener list. The listener is
	 * registered for all properties.
	 * 
	 * @param listener The PropertyChangeListener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
//		checkThread();
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
//		checkThread();
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
//		checkThread();
		if(oldValue != null && newValue != null && oldValue.equals(newValue))
		{
			return;
		}

		PropertyChangeEvent evt = new PropertyChangeEvent(source, propertyName,
				oldValue, newValue);

		// Loop over copy of list for thread safeness
		for(PropertyChangeListener oneListener: new ArrayList<PropertyChangeListener>(listeners))
		{
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
//		checkThread();
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
//		checkThread();
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
	
	// Check for thread safeness
//	protected Object getComp()
//	{
//		Object ret	= null;
//		try
//		{
//			Class<?>	clazz	= SReflect.classForName0("jadex.bridge.IComponentIdentifier", null);
//			ThreadLocal<?>	tl	= (ThreadLocal<?>)clazz.getField("LOCAL").get(null);
//			ret	= tl.get();
//		}
//		catch(Throwable t)
//		{
//		}
//		
//		return ret;
//	}
//	
//	protected void	checkThread()
//	{
//		assert SUtil.equals(comp, getComp());
//	}
}
