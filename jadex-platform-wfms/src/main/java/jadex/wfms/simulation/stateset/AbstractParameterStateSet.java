package jadex.wfms.simulation.stateset;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public abstract class AbstractParameterStateSet implements IParameterStateSet
{
	protected String name;
	
	private Set listeners;
	
	public AbstractParameterStateSet()
	{
		listeners = new HashSet();
	}
	
	/** Returns the parameter name.
	 *  
	 *  @return parameter name
	 */
	public String getParameterName()
	{
		return name;
	}
	
	/**
	 * Adds a change listener for state set changes.
	 * @param listener the listener
	 */
	public void addStateChangeListener(ChangeListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Removes a change listener for state set changes.
	 * @param listener the listener
	 */
	public void removeStateChangeListener(ChangeListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index)
	{
		return null;
	}
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @param initialState initial state of the parameter
	 * @return the specified state
	 */
	public Object getState(long index, Object initialState)
	{
		return getState(index);
		
	}
	
	/**
	 * Returns a description of the given state.
	 * 
	 * @param index index of the state
	 * @return description
	 */
	public String getStateDescription(long index)
	{
		return String.valueOf(getState(index));
	}
	
	protected void fireStateChange(Object source)
	{
		for (Iterator it = listeners.iterator(); it.hasNext(); )
		{
			ChangeListener listener = (ChangeListener) it.next();
			listener.stateChanged(new ChangeEvent(source));
		}
	}
}
