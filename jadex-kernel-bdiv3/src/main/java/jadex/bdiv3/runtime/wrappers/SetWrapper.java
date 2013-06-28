package jadex.bdiv3.runtime.wrappers;

import jadex.bdiv3.runtime.impl.BDIAgentInterpreter;

import java.util.Set;

/**
 * 
 */
public class SetWrapper <T> extends CollectionWrapper<T> implements Set<T>
{
	/**
	 *  Create a new set wrapper.
	 */
	public SetWrapper(Set<T> delegate, BDIAgentInterpreter interpreter, 
		String addevent, String remevent, String changeevent)
	{
		super(delegate, interpreter, addevent, remevent, changeevent);
	}
}
