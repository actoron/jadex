package jadex.wfms.simulation.stateholder;

public interface IParameterStateSet
{
	/** Returns the parameter name.
	 *  
	 *  @return parameter name
	 */
	public String getParameterName();
	
	/**
	 * Gets the number of states in this state set.
	 * @return number of states
	 */
	public long getStateCount();
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index);
}
