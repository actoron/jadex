package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.runtime.IBeliefListener;
import jadex.rules.eca.ChangeInfo;

/**
 *  Belief listener adapter that implements empty all methods.
 */
public class BeliefAdapter<T> implements IBeliefListener<T>
{
	/**
	 *  Invoked when a belief has been changed.
	 *  @param event The change event.
	 */ 
	public void beliefChanged(ChangeInfo<T> info)
	{
	}
	
	/**
	 *  Invoked when a fact has been added.
	 *  The new fact is contained in the agent event.
	 *  @param event The change event.
	 */
	public void factAdded(ChangeInfo<T> info)
	{
	}

	/**
	 *  Invoked when a fact has been removed.
	 *  The removed fact is contained in the agent event.
	 *  @param event The change event.
	 */
	public void factRemoved(ChangeInfo<T> info)
	{
	}

	/**
	 *  Invoked when a fact in a belief set has changed (i.e. bean event).
	 *  @param value The new value.
	 *  @param oldvalue The old value.
	 *  @param info Extra info (such as the index of the element if applicable).
	 */  
	public void factChanged(ChangeInfo<T> info)
	{
	}
}
