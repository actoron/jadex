package jadex.commons.collection;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 *  Class extending java.util.Properties to preserve order
 *  by redirecting calls to an internal linked hash map.
 *
 */
public class OrderedProperties extends Properties
{
	/** The internal map. */
	protected LinkedHashMap<Object, Object> map = new LinkedHashMap<Object, Object>();
	
	/**
	 *  Delegate.
	 */
	public int size()
	{
		return map.size();
	}

	/**
	 *  Delegate.
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}
	
	/**
	 *  Delegate.
	 */
	public boolean containsKey(Object key)
	{
		return map.containsKey(key);
	}
	
	/**
	 *  Delegate.
	 */
	public boolean containsValue(Object value)
	{
		return map.containsValue(value);
	}
	
	/**
	 *  Delegate.
	 */
	public Object get(Object key)
	{
		return map.get(key);
	}
	
	/**
	 *  Delegate.
	 */
	public Object put(Object key, Object value)
	{
		return map.put(key, value);
	}
	
	/**
	 *  Delegate.
	 */
	public Object remove(Object key)
	{
		return map.remove(key);
	}
	
	/**
	 *  Delegate.
	 */
	public void putAll(Map<? extends Object, ? extends Object> m)
	{
		map.putAll(m);
	}
	
	/**
	 *  Delegate.
	 */
	public void clear()
	{
		map.clear();
	}
	
	/**
	 *  Delegate.
	 */
	public Set<Object> keySet()
	{
		return map.keySet();
	}
	
	/**
	 *  Delegate.
	 */
	public Collection<Object> values()
	{
		return map.values();
	}
	
	/**
	 *  Delegate.
	 */
	public Set<java.util.Map.Entry<Object, Object>> entrySet()
	{
		return map.entrySet();
	}

}
