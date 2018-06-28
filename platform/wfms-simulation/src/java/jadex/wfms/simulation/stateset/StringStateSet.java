package jadex.wfms.simulation.stateset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringStateSet extends AbstractParameterStateSet
{
	private List strings;
	
	public StringStateSet(String parameterName)
	{
		this.name = parameterName;
		strings = new ArrayList();
	}
	
	/** Returns the parameter type.
	 *  
	 *  @return parameter type
	 */
	public Class getParameterType()
	{
		return String.class;
	}
	
	/**
	 * Adds a new String to the set.
	 * @param string the new String
	 */
	public boolean addString(String string)
	{
		if (!strings.contains(string))
		{
			strings.add(string);
			Collections.sort(strings);
			fireStateChange(string);
			return true;
		}
		return false;
	}
	
	/**
	 * Removes a String from the set.
	 * @param index index of the String
	 */
	public void removeString(int index)
	{
		String string = (String) strings.remove(index);
		fireStateChange(string);
	}
	
	/**
	 *  Attempts to set a string in the set.
	 * 	@param index index of the string
	 *  @param string the new string
	 */
	public void setString(int index, String string)
	{
		if (!strings.contains(string))
			strings.set(index, string);
	}
	
	public List getStrings()
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
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index)
	{
		return strings.get((int) index);
	}
}
