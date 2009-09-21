package jadex.wfms.simulation.stateholder;

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
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index)
	{
		return states.get((int) (index % states.size()));
	}
}
