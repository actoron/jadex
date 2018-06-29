package jadex.rules.examples.fibonacci;

import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.beans.PropertyChangeSupport;

/**
 *  The fibonacci number java representation.
 */
public class FibonacciNumber
{
	/** The sequence number. */
	protected int sequence;

	/** The value. */
	protected long value;

	/** The property change support. */
	protected PropertyChangeSupport pcs;
	
	/**
	 *  Create a new FibonacciNumber.
	 *  @param sequence The 
	 */
	public FibonacciNumber(int sequence)
	{
		this.sequence = sequence;
		this.value = -1;
		this.pcs = new PropertyChangeSupport(this);
	}

	/**
	 *  Get the sequence number.
	 *  @return The sequence number.
	 */
	public int getSequence()
	{
		return this.sequence;
	}

	/**
	 *  Set the value.
	 *  @param value The value.
	 */
	public void setValue(long value)
	{
		this.value = value;
		pcs.firePropertyChange("value", Long.valueOf(-1), Long.valueOf(value));
	}

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public long getValue()
	{
		return this.value;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Fibonacci(" + this.sequence + "/" + this.value + ")";
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
