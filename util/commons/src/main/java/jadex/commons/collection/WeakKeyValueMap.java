package jadex.commons.collection;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 *  HashMap with weak reference on both sides.
 */
public class WeakKeyValueMap<K, V>	implements Map<K, V>
{
	/** The internal delegate map. */
	protected WeakHashMap<K, WeakReference<V>> content = new WeakHashMap<K, WeakReference<V>>();
	
	public WeakKeyValueMap()
	{
		super();
	}
	
	public WeakKeyValueMap(Map<? extends K, ? extends V> m)
	{
		super();
		putAll(m);
	}
	
	/**
	 *  Returns the size of the map.
	 */
	public int size()
	{
		return content.size();
	}
	
	/**
	 *  Returns if empty.
	 */
	public boolean isEmpty()
	{
		return content.isEmpty();
	}
	
	/**
	 *  Returns if key is contained.
	 */
	public boolean containsKey(Object key)
	{
		return content.containsKey(key);
	}

	/**
	 *  Returns if value is contained.
	 */
	public boolean containsValue(Object value)
	{
		for (Map.Entry<K, WeakReference<V>> entry : content.entrySet())
		{
			if (value.equals(entry.getValue().get()))
				return true;
		}
		return false;
	}

	/**
	 *  Gets the value for key.
	 */
	public V get(Object key)
	{
		WeakReference<V> r = content.get(key);
		return r != null ? r.get() : null;
	}
	
	/**
	 *  Adds value for key.
	 */
	public V put(K key, V value)
	{
		content.put(key, new WeakReference<V>(value));
		return value;
	}
	
	/**
	 *  Removes key.
	 */
	public V remove(Object key)
	{
		WeakReference<V> r = content.remove(key);
		return r != null ? r.get() : null;
	}
	
	/**
	 *  Adds other map content.
	 */
	public void putAll(Map<? extends K, ? extends V> m)
	{
		if (m != null)
		{
			for (Map.Entry<? extends K, ? extends V> entry : m.entrySet())
				put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 *  Clears map.
	 */
	public void clear()
	{
		content.clear();
	}

	/**
	 *  Returns the key set.
	 */
	public Set<K> keySet()
	{
		return content.keySet();
	}

	/**
	 *  Returns the values.
	 */
	public Collection<V> values()
	{
		return null;
	}

	/**
	 *  Returns the entry set.
	 */
	public Set<Entry<K, V>> entrySet()
	{
		HashSet<Entry<K, V>> ret = new HashSet<Map.Entry<K,V>>();
		for (Map.Entry<K, WeakReference<V>> entry : content.entrySet())
		{
			// Hard reference here for consistency.
			final K key = entry.getKey();
			final WeakReference<V> value = entry.getValue();
			Map.Entry<K, V> newentry = new Map.Entry<K, V>()
			{
				public K getKey()
				{
					return key;
				}

				public V getValue()
				{
					return value != null ? value.get() : null;
				}

				public V setValue(V value)
				{
					return put(key, value);
				}
			};
			ret.add(newentry);
		}
		return ret;
	}
}
