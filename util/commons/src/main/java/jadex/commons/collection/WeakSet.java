package jadex.commons.collection;


import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 *  A weak set for entries that will be automatically removed when
 *  no references to them are existing any more.
 */
public class WeakSet<T> extends AbstractSet<T>	implements Serializable, Cloneable
{
	//-------- attributes ---------

	/** The set which will be used for element storage. */
	protected transient Set set;

	/** The reference queue used to get object removal notifications. */
	protected transient ReferenceQueue queue = new ReferenceQueue();

	//-------- constructors ---------

	/**
	 * Construct a WeakSet based on a HashSet.
	 */
	public WeakSet()
	{
		this.set = SCollection.createHashSet();
	}

	//-------- methods ---------

	/**
	 *  Return the size of the set.
	 *  @return The size of the set.
	 */
	public int size()
	{
		expungeStaleEntries();
		return set.size();
	}

	/**
	 *  Return an iteration over the elements in the set.
	 *  @return An iteration over the elements in the set.
	 */
	public Iterator iterator()
	{
		// todo: is this implementation sound???
		expungeStaleEntries();
		return new Iterator()
		{
			Iterator iter = set.iterator();
			Object next = null;

			public boolean hasNext()
			{
				while(next==null && iter.hasNext())
					this.next = ((WeakObject)iter.next()).get();
				return next!=null;
			}

			public Object next()
			{
				if(!hasNext())
					throw new NoSuchElementException();

				// hasNext() has the side-effect of setting the next element!
				Object ret = this.next;
				this.next = null;
				return ret;
			}

			public void remove()
			{
				throw new UnsupportedOperationException("Remove method not supported for iterator of weak set.");
			}
		};
	}

	/**
	 *  Convert the set to an array.
	 */
	// Overriden, because AbstractCollection implementation relies on constant size.
	public Object[] toArray()
	{
		Object[] result = new Object[size()];
		int i	= 0;
		Iterator it	= iterator();
		for(; it.hasNext(); i++)
			result[i] = it.next();
		
		// Reallocate array, when some elements have been garbage collected (shouldn't happen often).
		if(i<result.length)
		{
			Object[]	result2	= new Object[i];
			System.arraycopy(result, 0, result2, 0, i);
			result	= result2;
		}
		
		return result;
	}

    /**
	 *  Convert the set to an array.
	 */
	// Overriden, because AbstractCollection implementation relies on constant size.
	public Object[] toArray(Object result[])
    {
		int	size	= size();
		if(result.length<size)
			result	= (Object[])java.lang.reflect.Array.newInstance(result.getClass().getComponentType(), size);
		
		int i	= 0;
		Iterator it	= iterator();
		for(; it.hasNext(); i++)
			result[i] = it.next();
		
		// Reallocate array, when some elements have been garbage collected (shouldn't happen often).
		if(i<result.length)
		{
			Object[]	result2	= (Object[])java.lang.reflect.Array.newInstance(result.getClass().getComponentType(), i);
			System.arraycopy(result, 0, result2, 0, i);
			result	= result2;
		}
		
		return result;
    }
	/**
	 * Add an element to the set.
	 * @param obj Element to add to the set.
	 * @return True if the element was added.
	 */

	public boolean add(final Object obj)
	{
		if(obj==null)
			throw new IllegalArgumentException("Must not be null.");
		expungeStaleEntries();
		return set.add(new WeakObject(obj, queue));
	}


	/**
	 *  Returns true if this set contains no elements.
	 *  @return true if this set contains no elements.
	 */
	public boolean isEmpty()
	{
		expungeStaleEntries();
		return set.isEmpty();
	}


	/**
	 *  Returns true if this set contains the specified element.
	 *  @param obj Element whose presence in this set is to be tested.
	 *  @return true if this set contains the specified element.
	 */
	public boolean contains(final Object obj)
	{
		if(obj==null)
			throw new IllegalArgumentException("Must not be null.");
		expungeStaleEntries();
		return set.contains(new WeakObject(obj));
	}


	/**
	 *  Removes the given element from this set if it is present.
	 *  @param obj Object to be removed from this set, if present.
	 *  @return true if the set contained the specified element.
	 */
	public boolean remove(final Object obj)
	{
		if(obj==null)
			throw new IllegalArgumentException("Must not be null.");
		expungeStaleEntries();
		return set.remove(new WeakObject(obj));
	}

	/**
	 *  Removes all of the elements from this set.
	 */
	public void clear()
	{
		serialized_set	= null;
		set.clear();
	}

	/**
	 * Returns a shallow copy of this WeakSet instance: the elements themselves are not cloned.
	 * @return A shallow copy of this set.
	 */
	public Object clone()
	{
		expungeStaleEntries();
		try
		{
			return super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			throw new InternalError();
		}
	}

	/**
	 *  Remove garbage collected entries.
	 */
	protected final void expungeStaleEntries()
	{
		serialized_set	= null;
		WeakObject weak;
		while((weak = (WeakObject)queue.poll())!=null)
		{
			set.remove(weak);
		}
	}

	//-------- serialization handling --------

	protected Set	serialized_set;
	
	/**
	 *  Perform special handling on serialization.
	 */
	protected Object	writeReplace() throws ObjectStreamException
	{
		// Extract weak references as they are not serializable.
		expungeStaleEntries();
		this.serialized_set	= SCollection.createHashSet();
		for(Iterator it=set.iterator(); it.hasNext(); )
		{
			Object	next = ((WeakObject)it.next()).get();
			if(next!=null)
				serialized_set.add(next);
		}
		return this;
	}

	/**
	 *  Perform special handling on serialization.
	 */
	protected Object	readResolve() throws ObjectStreamException
	{
		// Restore weak references as they are not serialized.
		this.set	= SCollection.createHashSet();
		this.queue	= new ReferenceQueue();
		for(Iterator it=serialized_set.iterator(); it.hasNext(); )
		{
			set.add(new WeakObject(it.next(), queue));
		}
		this.serialized_set	= null;
		return this;
	}
}

