package jadex.wfms.simulation.stateset;

import javax.swing.event.ChangeListener;

public interface IParameterStateSet
{
	/** Returns the parameter name.
	 *  
	 *  @return parameter name
	 */
	public String getParameterName();
	
	/** Returns the parameter type.
	 *  
	 *  @return parameter type
	 */
	public Class getParameterType();
	
	/**
	 * Returns the number of states in this state set.
	 * @return number of states
	 */
	public long getStateCount();
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @param initialState initial state of the parameter
	 * @return the specified state
	 */
	public Object getState(long index, Object initialState);
	
	/**
	 * Returns a description of the given state.
	 * 
	 * @param index index of the state
	 * @return description
	 */
	public String getStateDescription(long index);
	
	/**
	 * Adds a change listener for state set changes.
	 * @param listener the listener
	 */
	public void addStateChangeListener(ChangeListener listener);
	
	/**
	 * Removes a change listener for state set changes.
	 * @param listener the listener
	 */
	public void removeStateChangeListener(ChangeListener listener);
}
