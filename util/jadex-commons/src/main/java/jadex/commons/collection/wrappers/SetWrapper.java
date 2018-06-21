package jadex.commons.collection.wrappers;

import java.util.Set;

/**
 * 
 */
public abstract class SetWrapper <T> extends CollectionWrapper<T> implements Set<T>
{
//	/**
//	 *  Create a new set wrapper.
//	 */
//	public SetWrapper(Set<T> delegate, BDIAgentInterpreter interpreter, 
//		String addevent, String remevent, String changeevent, MBelief mbel)
//	{
//		super(delegate, interpreter, addevent, remevent, changeevent, mbel);
//	}
//	
//	/**
//	 *  Create a new set wrapper.
//	 */
//	public SetWrapper(Set<T> delegate, BDIAgentInterpreter interpreter, 
//		EventType addevent, EventType remevent, EventType changeevent, MBelief mbel)
//	{
//		super(delegate, interpreter, addevent, remevent, changeevent, mbel);
//	}
	
	/**
	 *  Create a new wrapper.
	 *  @param delegate The delegate.
	 */
	public SetWrapper(Set<T> delegate)
	{
		super(delegate);
	}
}
