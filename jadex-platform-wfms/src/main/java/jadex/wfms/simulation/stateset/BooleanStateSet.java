package jadex.wfms.simulation.stateset;

import java.util.ArrayList;
import java.util.List;

public class BooleanStateSet extends AbstractParameterStateSet
{
	private List states;
	
	public BooleanStateSet(String parameterName)
	{
		this.name = parameterName;
		states = new ArrayList();
	}
	
	/** Returns the parameter type.
	 *  
	 *  @return parameter type
	 */
	public Class getParameterType()
	{
		return Boolean.class;
	}
	
	/**
	 * Adds a new state to the set.
	 * @param state new state
	 */
	public void addState(Boolean state)
	{
		if (!states.contains(state))
		{
			states.add(state);
			fireStateChange(state);
		}
	}
	
	/**
	 * Removes a state from the set
	 * @param state the state
	 */
	public void removeState(Boolean state)
	{
		states.remove(state);
		fireStateChange(state);
	}
	
	/**
	 * Tests if a state is contained in this set.
	 * @param state the state being tested.
	 * @return true if the set contains the state, false otherwise
	 */
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
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index)
	{
		return states.get((int) (index % states.size()));
	}
}
