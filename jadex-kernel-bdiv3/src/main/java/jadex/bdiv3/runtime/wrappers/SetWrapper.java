package jadex.bdiv3.runtime.wrappers;

import jadex.rules.eca.RuleSystem;

import java.util.Set;

/**
 * 
 */
public class SetWrapper <T> extends CollectionWrapper<T> implements Set<T>
{
	/**
	 *  Create a new set wrapper.
	 */
	public SetWrapper(Set<T> delegate, RuleSystem rulesystem, 
		String addevent, String remevent, String changeevent)
	{
		super(delegate, rulesystem, addevent, remevent, changeevent);
	}
}
