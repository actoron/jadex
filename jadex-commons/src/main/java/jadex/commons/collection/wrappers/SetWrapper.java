package jadex.bdiv3.runtime.wrappers;

import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.runtime.impl.BDIAgentInterpreter;
import jadex.rules.eca.EventType;

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
		String addevent, String remevent, String changeevent, MBelief mbel)
	{
		super(delegate, interpreter, addevent, remevent, changeevent, mbel);
	}
	
	/**
	 *  Create a new set wrapper.
	 */
	public SetWrapper(Set<T> delegate, BDIAgentInterpreter interpreter, 
		EventType addevent, EventType remevent, EventType changeevent, MBelief mbel)
	{
		super(delegate, interpreter, addevent, remevent, changeevent, mbel);
	}
}
