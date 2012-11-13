package jadex.wfms.simulation.stateset;

import jadex.wfms.parametertypes.MultiListChoice;

import java.util.ArrayList;
import java.util.List;

public class ResolvableMultiListChoiceStateSet extends AbstractParameterStateSet
{
	private Object[] choices;
	
	private List selectionSets;
	
	public ResolvableMultiListChoiceStateSet(String parameterName, Object[] choices)
	{
		this.name = parameterName;
		this.choices = choices;
		selectionSets = new ArrayList();
	}
	
	/** Returns the parameter type.
	 *  
	 *  @return parameter type
	 */
	public Class getParameterType()
	{
		return MultiListChoice.class;
	}
	
	/**
	 * Adds a new selection set to the set.
	 * @param selection the new selection set
	 */
	public void addSelectionSet(Object[] selectionSet)
	{
		if (!selectionSets.contains(selectionSet))
		{
			selectionSets.add(selectionSet);
			fireStateChange(selectionSet);
		}
	}
	
	/**
	 * Removes a selection set from the set.
	 * @param selection set the selection set
	 */
	public void removeSelectionSet(Object[] selectionSet)
	{
		selectionSets.remove(selectionSet);
		fireStateChange(selectionSet);
	}
	
	public List getSelectionSets()
	{
		return selectionSets;
	}
	
	/**
	 * Gets the number of states in this holder.
	 * @return number of states
	 */
	public long getStateCount()
	{
		return selectionSets.size();
	}
	
	/**
	 * Returns a specific state.
	 * @param index index of the state
	 * @return the specified state
	 */
	public Object getState(long index)
	{
		return selectionSets.get((int) index);
	}
	
	public Object[] getChoices()
	{
		return choices;
	}
}
