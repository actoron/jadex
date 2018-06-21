package jadex.commons.collection.wrappers;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 	Wrap a map and call template methods on modification.
 */
public abstract class MapWrapper<T, E> implements Map<T, E>
{
	//-------- attributes --------
	
	/** The delegate map. */
	protected Map<T, E> delegate;
	
	//-------- constructors --------
	
	/**
	 *  Create a new collection wrapper.
	 */
	public MapWrapper(Map<T, E> delegate)
	{
		this.delegate = delegate;
	}
	
	//-------- Map interface --------
	
	/** 
	 * 
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
	public boolean containsKey(Object key)
	{
		return delegate.containsKey(key);
	}

	/** 
	 * 
	 */
	public boolean containsValue(Object value)
	{
		return delegate.containsValue(value);
	}

	/** 
	 * 
	 */
	public E get(Object key)
	{
		return delegate.get(key);
	}

	/** 
	 * 
	 */
	public E put(final T key, final E value)
	{
		boolean	contained	= delegate.containsKey(key);
		E ret = delegate.put(key, value);
		if(contained)
		{
			entryChanged(key, ret, value);
		}
		else
		{
			entryAdded(key, value);
		}
		return ret;
	}

	/** 
	 * 
	 */
	public E remove(Object key)
	{
		boolean	contained	= delegate.containsKey(key);
		E ret = delegate.remove(key);
		if(contained)
		{
			entryRemoved((T)key, ret);
		}
		return ret;
	}

	/** 
	 * 
	 */
	public void putAll(Map<? extends T, ? extends E> m)
	{
		delegate.putAll(m);
		entriesAdded(((Map<T, E>)m).entrySet());
	}

	/** 
	 * 
	 */
	public void clear()
	{
		Set<java.util.Map.Entry<T, E>> s = entrySet();
		delegate.clear();
		entriesRemoved(s);
	}

	/** 
	 * 
	 */
	public Set<T> keySet()
	{
		return delegate.keySet();
	}

	/** 
	 * 
	 */
	public Collection<E> values()
	{
		return delegate.values();
	}

	/** 
	 * 
	 */
	public Set<java.util.Map.Entry<T, E>> entrySet()
	{
		return delegate.entrySet();
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
		if(obj instanceof MapWrapper)
		{
			ret = delegate.equals(((MapWrapper<?, ?>)obj).delegate);
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

	//-------- template methods --------
	
	/**
	 *  An entry was added to the map.
	 */
	protected abstract void	entryAdded(T key, E value);
	
	/**
	 *  An entry was removed from the map.
	 */
	protected abstract void	entryRemoved(T key, E value);
	
	/**
	 *  An entry was changed in the map.
	 */
	protected abstract void	entryChanged(T key, E oldvalue, E newvalue);

	/**
	 *  Entries were added to the map.
	 */
	protected void	entriesAdded(Set<Map.Entry<T, E>> entries)
	{
		for(Map.Entry<T, E> entry: entries)
		{
			entryAdded(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 *  Entries were removed from the map.
	 */
	protected void	entriesRemoved(Set<Map.Entry<T, E>> entries)
	{
		for(Map.Entry<T, E> entry: entries)
		{
			entryRemoved(entry.getKey(), entry.getValue());
		}		
	}
}
