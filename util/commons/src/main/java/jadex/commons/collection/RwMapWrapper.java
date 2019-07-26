package jadex.commons.collection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *  Thread-safe wrapper for maps that uses a read/write lock.
 */
public class RwMapWrapper<K, V> implements IRwMap<K, V>
{
	protected RwAutoLock rwautolock;
	
	/** The wrapped map. */
	protected Map<K, V> map;
	
	/**
	 *  Creates the wrapper.
	 * 
	 *  @param map The wrapped map.
	 */
	public RwMapWrapper(Map<K, V> map)
	{
		this(map, false);
	}
	
	/**
	 *  Creates the wrapper with a specific internal lock.
	 * 
	 *  @param map The wrapped map.
	 */
	public RwMapWrapper(Map<K, V> map, ReadWriteLock lock)
	{
		this.rwautolock = new RwAutoLock(lock);
		this.map = map;
	}
	
	/**
	 *  Creates the wrapper.
	 * 
	 *  @param map The wrapped map.
	 *  @param fair Set true for fair-mode lock.
	 */
	public RwMapWrapper(Map<K, V> map, boolean fair)
	{
		this(map, new ReentrantReadWriteLock(fair));
	}

	/**
	 *  Returns the size of the map.
	 */
	public int size()
	{
		try(IAutoLock l = rwautolock.readLock())
		{
			return map.size();
		}
	}

	/**
	 *  Returns if map is empty.
	 */
	public boolean isEmpty()
	{
		try(IAutoLock l = rwautolock.readLock())
		{
			return map.isEmpty();
		}
	}

	/**
	 *  Returns if the map contains a key.
	 */
	public boolean containsKey(Object key)
	{
		try(IAutoLock l = rwautolock.readLock())
		{
			return map.containsKey(key);
		}
	}

	/**
	 *  Returns if the map contains a value.
	 */
	public boolean containsValue(Object value)
	{
		try(IAutoLock l = rwautolock.readLock())
		{
			return map.containsValue(value);
		}
	}

	/**
	 *  Gets the value for the key.
	 */
	public V get(Object key)
	{
		try(IAutoLock l = rwautolock.readLock())
		{
			return map.get(key);
		}
	}

	/**
	 *  Puts a key-value pair.
	 */
	public V put(K key, V value)
	{
		try(IAutoLock l = rwautolock.writeLock())
		{
			return map.put(key, value);
		}
	}

	/**
	 *  Removes a key-value pair.
	 */
	public V remove(Object key)
	{
		try(IAutoLock l = rwautolock.writeLock())
		{
			return map.remove(key);
		}
	}

	/**
	 *  Puts all key-value pairs into map.
	 */
	public void putAll(Map<? extends K, ? extends V> m)
	{
		try(IAutoLock l = rwautolock.writeLock())
		{
			map.putAll(m);
		}
	}

	/**
	 *  Clears the map.
	 */
	public void clear()
	{
		try(IAutoLock l = rwautolock.writeLock())
		{
			map.clear();
		}
	}

	/**
	 *  Returns the key set.
	 *  Warning: Use manual locking.
	 */
	public Set<K> keySet()
	{
		return map.keySet();
	}

	/**
	 *  Returns the key values.
	 *  Warning: Use manual locking.
	 */
	public Collection<V> values()
	{
		return map.values();
	}

	/**
	 *  Returns the entry set.
	 *  Warning: Use manual locking.
	 */
	public Set<Entry<K, V>> entrySet()
	{
		return map.entrySet();
	}
	
	/**
	 *  Locks the read lock for resource-based locking.
	 */
	public IAutoLock readLock()
	{
		return rwautolock.readLock();
	}
	
	/**
	 *  Locks the write lock for resource-based locking.
	 */
	public IAutoLock writeLock()
	{
		return rwautolock.writeLock();
	}
	
	/**
	 *  Gets the read lock for manual locking.
	 */
	public Lock getReadLock()
	{
		return rwautolock.getReadLock();
	}
	
	/**
	 *  Gets the write lock for manual locking.
	 */
	public Lock getWriteLock()
	{
		return rwautolock.getWriteLock();
	}
	
	/**
	 *  Gets the internal lock.
	 *  @return The lock.
	 */
	public ReadWriteLock getLock()
	{
		return rwautolock.getLock();
	}
}
