package jadex.commons.collection;

import java.io.Closeable;
import java.io.IOException;
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
	/** The lock. */
	protected ReadWriteLock rwlock;
	
	/** The resource read unlock. */
	protected IAutoLock readunlock;
	
	/** The resource write unlock. */
	protected IAutoLock writeunlock;
	
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
		this.rwlock = lock;
		this.map = map;
		this.readunlock = new IAutoLock()
		{
			public void close()
			{
				release();
			}

			public void release()
			{
				rwlock.readLock().unlock();
			}
		};
		this.writeunlock = new IAutoLock()
		{
			public void close()
			{
				release();
			}
			
			public void release()
			{
				rwlock.writeLock().unlock();
			}
		};
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
		rwlock.readLock().lock();
		int ret = map.size();
		rwlock.readLock().unlock();
		return ret;
	}

	/**
	 *  Returns if map is empty.
	 */
	public boolean isEmpty()
	{
		rwlock.readLock().lock();
		boolean ret = map.isEmpty();
		rwlock.readLock().unlock();
		return ret;
	}

	/**
	 *  Returns if the map contains a key.
	 */
	public boolean containsKey(Object key)
	{
		rwlock.readLock().lock();
		boolean ret = map.containsKey(key);
		rwlock.readLock().unlock();
		return ret;
	}

	/**
	 *  Returns if the map contains a value.
	 */
	public boolean containsValue(Object value)
	{
		rwlock.readLock().lock();
		boolean ret = map.containsValue(value);
		rwlock.readLock().unlock();
		return ret;
	}

	/**
	 *  Gets the value for the key.
	 */
	public V get(Object key)
	{
		rwlock.readLock().lock();
		V ret = map.get(key);
		rwlock.readLock().unlock();
		return ret;
	}

	/**
	 *  Puts a key-value pair.
	 */
	public V put(K key, V value)
	{
		rwlock.writeLock().lock();
		V ret = map.put(key, value);
		rwlock.writeLock().unlock();
		return ret;
	}

	/**
	 *  Removes a key-value pair.
	 */
	public V remove(Object key)
	{
		rwlock.writeLock().lock();
		V ret = map.remove(key);
		rwlock.writeLock().unlock();
		return ret;
	}

	/**
	 *  Puts all key-value pairs into map.
	 */
	public void putAll(Map<? extends K, ? extends V> m)
	{
		rwlock.writeLock().lock();
		map.putAll(m);
		rwlock.writeLock().unlock();
	}

	/**
	 *  Clears the map.
	 */
	public void clear()
	{
		rwlock.writeLock().lock();
		map.clear();
		rwlock.writeLock().unlock();
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
		rwlock.readLock().lock();
		return readunlock;
	}
	
	/**
	 *  Locks the write lock for resource-based locking.
	 */
	public IAutoLock writeLock()
	{
		rwlock.writeLock().lock();
		return writeunlock;
	}
	
	/**
	 *  Gets the read lock for manual locking.
	 */
	public Lock getReadLock()
	{
		return rwlock.readLock();
	}
	
	/**
	 *  Gets the write lock for manual locking.
	 */
	public Lock getWriteLock()
	{
		return rwlock.writeLock();
	}
	
	/**
	 *  Gets the internal lock.
	 *  @return The lock.
	 */
	public ReadWriteLock getLock()
	{
		return rwlock;
	}
}
