package jadex.wfms.simulation.stateset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class StringArrayStateSet extends AbstractParameterStateSet
{
	private List stringarrays;
	
	public StringArrayStateSet(String parameterName)
	{
		this.name = parameterName;
		stringarrays = new ArrayList();
	}
	
	/** Returns the parameter type.
	 *  
	 *  @return parameter type
	 */
	public Class getParameterType()
	{
		return String[].class;
	}
	
	/**
	 * Adds a new String to the set.
	 * @param string the new String
	 */
	public void addString(String[] stringarray)
	{
		for (Iterator it = stringarrays.iterator(); it.hasNext(); )
			if (Arrays.equals(stringarray, Arrays.asList((String[]) it.next()).toArray()))
				return;
		stringarrays.add(stringarray);
		fireStateChange(stringarray);
	}
	
	/**
	 * Removes a String from the set.
	 * @param string the String
	 */
	public void removeString(String[] stringarray)
	{
		stringarrays.remove(stringarray);
		fireStateChange(stringarray);
	}
	
	public List getStringArrayss()
	{
		return stringarrays;
	}
	
	/**
	 * Gets the number of states in this holder.
	 * @return number of states
	 */
	public long getStateCount()
	{
		return stringarrays.size();
	}
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index)
	{
		return stringarrays.get((int) index);
	}
}
