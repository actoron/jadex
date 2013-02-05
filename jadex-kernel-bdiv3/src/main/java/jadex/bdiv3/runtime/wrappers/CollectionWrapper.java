package jadex.bdiv3.runtime.wrappers;

import jadex.rules.eca.Event;
import jadex.rules.eca.RuleSystem;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 *  Wrapper for collections. Creates rule events on add/remove/change operation calls.
 */
public class CollectionWrapper <T> implements Collection<T>
{
	/** The delegate list. */
	protected Collection<T> delegate;
	
	/** The rulesystem. */
	protected RuleSystem rulesystem;
	
	/** The add event name. */
	protected String addevent;
	
	/** The remove event name. */
	protected String remevent;
	
	/** The change event name. */
	protected String changeevent;
	
	/**
	 *  Create a new collection wrapper.
	 */
	public CollectionWrapper(Collection<T> delegate, RuleSystem rulesystem, 
		String addevent, String remevent, String changeevent)
	{
		this.delegate = delegate;
		this.rulesystem = rulesystem;
		this.addevent = addevent;
		this.remevent = remevent;
		this.changeevent = changeevent;
	}

	/**
	 *  Get the size.
	 */
	public int size()
	{
		return delegate.size();
	}

	/**
	 *  
	 */
	public boolean isEmpty()
	{
		return delegate.isEmpty();
	}

	/**
	 *  
	 */
	public boolean contains(Object o)
	{
		return delegate.contains(o);
	}

	/**
	 *  
	 */
	public Iterator<T> iterator()
	{
		return delegate.iterator();
	}

	/**
	 *  
	 */
	public Object[] toArray()
	{
		return delegate.toArray();
	}

	/**
	 *  
	 */
	public <T> T[] toArray(T[] a)
	{
		return delegate.toArray(a);
	}

	/**
	 *  
	 */
	public boolean add(T e)
	{
		boolean ret = delegate.add(e);
		if(ret)
			rulesystem.addEvent(new Event(addevent, e));
		return ret;
	}

	/**
	 *  
	 */
	public boolean remove(Object o)
	{
		boolean ret = delegate.remove(o);
		if(ret)
			rulesystem.addEvent(new Event(remevent, o));
		return ret;
	}

	/**
	 *  
	 */
	public boolean containsAll(Collection<?> c)
	{
		return delegate.containsAll(c);
	}

	/**
	 *  
	 */
	public boolean addAll(Collection<? extends T> c)
	{
		boolean ret = delegate.addAll(c);
		if(ret)
		{
			for(T t: c)
			{
				rulesystem.addEvent(new Event(addevent, t));
			}
		}	
		return ret;
	}

	/**
	 *  
	 */
	public boolean removeAll(Collection<?> c)
	{
		boolean ret = delegate.removeAll(c);
		if(ret)
		{
			for(Object t: c)
			{
				rulesystem.addEvent(new Event(remevent, t));
			}
		}	
		return ret;
	}

	/**
	 *  
	 */
	public boolean retainAll(Collection< ? > c)
	{
		// todo:
		return delegate.retainAll(c);
	}

	/**
	 *  
	 */
	public void clear()
	{
		T[] clone = delegate.toArray((T[])new Object[delegate.size()]);
		delegate.clear();
		for(Object t: clone)
		{
			rulesystem.addEvent(new Event(addevent, t));
		}
	}
	
	/** 
	 *  Get the hashcode of the object.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		return delegate.hashCode();
	}

	/** 
	 *  Test if this object equals another.
	 *  @param obj The other object.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof CollectionWrapper)
		{
			ret = delegate.equals(((CollectionWrapper)obj).delegate);
		}
		else if(obj instanceof Map)
		{
			ret = delegate.equals(obj);
		}
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return delegate.toString();
	}
}
