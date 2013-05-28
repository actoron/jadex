package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.IBeliefListener;

/**
 *  Belief listener adapter that implements empty all methods.
 */
public class BeliefAdapter implements IBeliefListener
{
	/**
	 *  Invoked when a belief has been changed.
	 *  @param event The change event.
	 */ 
	public void beliefChanged(Object value)
	{
	}
	
	/**
	 *  Invoked when a fact has been added.
	 *  The new fact is contained in the agent event.
	 *  @param event The change event.
	 */
	public void factAdded(Object value)
	{
	}

	/**
	 *  Invoked when a fact has been removed.
	 *  The removed fact is contained in the agent event.
	 *  @param event The change event.
	 */
	public void factRemoved(Object value)
	{
	}

	/**
	 *  Invoked when a fact in a belief set has changed (i.e. bean event).
	 *  @param event The change event.
	 */ 
	public void factChanged(Object value, Object oldvalue, Object info)
	{
	}
}
