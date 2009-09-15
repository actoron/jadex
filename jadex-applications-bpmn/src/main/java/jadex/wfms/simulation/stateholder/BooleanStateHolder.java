package jadex.wfms.simulation.stateholder;

import java.util.ArrayList;
import java.util.List;

public class BooleanStateHolder implements IParameterStateHolder
{
	private List states;
	
	private int currentState;
	
	public BooleanStateHolder()
	{
		states = new ArrayList();
		this.currentState = 0;
	}
	
	public void addState(Boolean state)
	{
		if (!states.contains(state))
			states.add(state);
	}
	
	public void removeState(Boolean state)
	{
		states.remove(state);
	}
	
	public boolean hasState(Boolean state)
	{
		return states.contains(state);
	}
	
	/**
	 * Gets the number of states in this holder.
	 * @return number of states
	 */
	public long getStateCount()
	{
		return states.size();
	}
	
	/**
	 * Resets the state holder to the first available state.
	 */
	public void reset()
	{
		currentState = 0;
	}
	
	/**
	 * Switches to the next available state.
	 */
	public void nextState()
	{
		currentState = ((currentState + 1) % states.size());
	}
	
	/**
	 * Returns the current state
	 * @return current state
	 */
	public long getCurrentState()
	{
		return currentState;
	}
	
	/**
	 * Test if the holder is in the final state.
	 * @return true, if in the final state
	 */
	public boolean finalState()
	{
		return ((currentState + 1) == states.size());
	}
	
	/**
	 * Returns the current state.
	 * @return the current state
	 */
	public Object getState()
	{
		return states.get(currentState);
	}
}
