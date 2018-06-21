package jadex.commons;

import java.util.Iterator;

/**
 *  Needed because Java does not support enhanced for loop
 *  with Iterator elements :-( why? Only Iterable is supported.
 */
public class IterableIteratorWrapper<T> implements Iterable<T>
{
	/** The iterator. */
	protected Iterator<T> iter;
	
	/**
	 *  Create a new wrapper.
	 *  @param iter The iterator.
	 */
	public IterableIteratorWrapper(Iterator<T> iter)
	{
		this.iter = iter;
	}

	/**
	 *  Get the iterator.
	 *  @return The iterator.
	 */
	public java.util.Iterator<T> iterator()
	{
		return SReflect.getIterator(iter);
	}
}
