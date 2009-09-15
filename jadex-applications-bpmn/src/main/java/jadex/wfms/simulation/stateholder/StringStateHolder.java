package jadex.wfms.simulation.stateholder;

import java.util.SortedSet;
import java.util.TreeSet;

public class StringStateHolder implements IParameterStateHolder
{
	private SortedSet strings;
	
	private SortedSet state;
	
	public StringStateHolder()
	{
		strings = new TreeSet();
		state = strings;
	}
	
	public void addString(String string)
	{
		strings.add(string);
	}
	
	public void removeString(String string)
	{
		strings.remove(string);
	}
	
	public SortedSet getStrings()
	{
		return strings;
	}
	
	/**
	 * Gets the number of states in this holder.
	 * @return number of states
	 */
	public long getStateCount()
	{
		return strings.size();
	}
	
	/**
	 * Resets the state holder to the first available state.
	 */
	public void reset()
	{
		state = strings;
	}
	
	/**
	 * Switches to the next available state.
	 */
	public void nextState()
	{
		state = state.headSet(state.last());
	}
	
	/**
	 * Returns the current state
	 * @return current state
	 */
	public long getCurrentState()
	{
		return strings.size() - state.size();
	}
	
	/**
	 * Test if the holder is in the final state.
	 * @return true, if in the final state
	 */
	public boolean finalState()
	{
		return state.isEmpty();
	}
	
	/**
	 * Returns the current state.
	 * @return the current state
	 */
	public Object getState()
	{
		return state.last();
	}
}
