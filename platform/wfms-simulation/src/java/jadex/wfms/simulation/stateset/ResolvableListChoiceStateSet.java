package jadex.wfms.simulation.stateset;

import jadex.wfms.parametertypes.ListChoice;

import java.util.ArrayList;
import java.util.List;

public class ResolvableListChoiceStateSet extends AbstractParameterStateSet
{
	private Object[] choices;
	
	private List selections;
	
	public ResolvableListChoiceStateSet(String parameterName, Object[] choices)
	{
		this.name = parameterName;
		this.choices = choices;
		selections = new ArrayList();
	}
	
	/** Returns the parameter type.
	 *  
	 *  @return parameter type
	 */
	public Class getParameterType()
	{
		return ListChoice.class;
	}
	
	/**
	 * Adds a new selection to the set.
	 * @param selection the new selection
	 */
	public void addSelection(Object selection)
	{
		if (!selections.contains(selection))
		{
			selections.add(selection);
			fireStateChange(selection);
		}
	}
	
	/**
	 * Removes a selection from the set.
	 * @param selection the selection
	 */
	public void removeSelection(Object selection)
	{
		selections.remove(selection);
		fireStateChange(selection);
	}
	
	public List getSelections()
	{
		return selections;
	}
	
	/**
	 * Checks whether a selection is in the set.
	 * @param selection the selection
	 * @return true if the choice is in the set, false otherwise
	 */
	public boolean hasSelection(Object selection)
	{
		return selections.contains(selection);
	}
	
	/**
	 * Gets the number of states in this holder.
	 * @return number of states
	 */
	public long getStateCount()
	{
		return selections.size();
	}
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index)
	{
		return selections.get((int) index);
	}
	
	public Object[] getChoices()
	{
		return choices;
	}
}
