package jadex.wfms.simulation;

public interface IParameterStateHolder
{
	/**
	 * Gets the number of states in this holder.
	 * @return number of states
	 */
	public long getStateCount();
	
	/**
	 * Resets the state holder to the first available state.
	 */
	public void reset();
	
	/**
	 * Switches to the next available state.
	 */
	public void nextState();
	
	/**
	 * Test if the holder is in the final state.
	 * @return true, if in the final state
	 */
	public boolean finalState();
	
	/**
	 * Returns the current state.
	 * @return the current state
	 */
	public Object getState();
}
