package jadex.wfms.simulation.stateset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.Map;

public class ActivityStateController
{
	private String name;
	
	private long[] currentStates;
	
	private List stateSets;
	
	public ActivityStateController(String activityName)
	{
		this.name = activityName;
		stateSets = new ArrayList();
		currentStates = new long[0];
	}
	
	public String getActivityName()
	{
		return name;
	}
	
	/**
	 * Adds a parameter state set
	 * @param stateSet new state set
	 */
	public void addStateSet(IParameterStateSet stateSet)
	{
		stateSets.add(stateSet);
		currentStates = new long[stateSets.size()];
		reset();
	}
	
	/**
	 * Adds a parameter state set
	 * @param stateSet new state set
	 */
	public void removeStateSet(IParameterStateSet stateSet)
	{
		stateSets.remove(stateSet);
		currentStates = new long[stateSets.size()];
		if (stateSets.size() != 0)
			reset();
	}
	
	/**
	 * Resets the state controller to the first available state.
	 */
	public void reset()
	{
		assert currentStates.length > 0;
		Arrays.fill(currentStates, 0);
	}
	
	/**
	 * Switches to the next available state.
	 */
	public void nextState()
	{
		assert currentStates.length > 0;
		incrementStateRec(0);
	}
	
	/**
	 * Test if the controller is in the final state.
	 * @return true, if in the final state
	 */
	public boolean finalState()
	{
		for (int i = 0; i < currentStates.length; ++i)
		{
			if (!(currentStates[i] == ((IParameterStateSet) stateSets.get(i)).getStateCount() - 1))
				return false;
		}
		return true;
	}
	
	/**
	 * Returns the number of combinations of parameter states
	 * for the activity.
	 * @return number of combinations of states
	 */
	public long getStateCount()
	{
		if (stateSets.size() == 0)
			return 0;
		long ret = 1;
		for (Iterator it = stateSets.iterator(); it.hasNext(); )
		{
			IParameterStateSet stateSet = (IParameterStateSet) it.next();
			ret *= stateSet.getStateCount();
		}
		return ret;
	}
	
	/**
	 * Returns the current state for the activity.
	 * @return current state of the activity
	 */
	public Map getActivityState(Map parameterValues)
	{
		Map ret = new HashMap();
		for (int i = 0; i < currentStates.length; ++i)
		{
			IParameterStateSet stateSet = (IParameterStateSet) stateSets.get(i);
			ret.put(stateSet.getParameterName(), stateSet.getState(currentStates[i], parameterValues.get(stateSet.getParameterName())));
		}
		return ret;
	}
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer(name + " [");
		for (int i = 0; i < currentStates.length; ++i)
		{
			IParameterStateSet stateSet = (IParameterStateSet) stateSets.get(i);
			buffer.append(stateSet.getStateDescription(currentStates[i]));
			buffer.append(", ");
		}
		buffer.setLength(buffer.length() - 2);
		buffer.append("]");
		return buffer.toString();
	}
	
	private void incrementStateRec(int index)
	{
		if (currentStates[index] == (((IParameterStateSet) stateSets.get(index)).getStateCount() - 1))
		{
			if (index == currentStates.length - 1)
				return;
			currentStates[index] = 0;
			incrementStateRec(index + 1);
		}
		else
			++currentStates[index];
		
		/*if (currentStates[index] + 2 < ((IParameterStateSet) stateSets.get(index)).getStateCount())
			++currentStates[index];
		else
		{
			if (index != (currentStates.length - 1))
			{
				currentStates[index] = 0;
				incrementStateRec(index + 1);
			}
		}*/
	}
}
