package jadex.commons.collection;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 *  Interface for thread-safe maps using a read-write lock.
 *
 */
public interface IRwMap<K, V> extends Map<K, V>
{
	/**
	 *  Locks the read lock for resource-based locking.
	 */
	public IAutoLock readLock();
	
	/**
	 *  Locks the write lock for resource-based locking.
	 */
	public IAutoLock writeLock();
	
	/**
	 *  Gets the read lock for manual locking.
	 */
	public Lock getReadLock();
	
	/**
	 *  Gets the write lock for manual locking.
	 */
	public Lock getWriteLock();
	
	/**
	 *  Gets the internal lock.
	 *  @return The lock.
	 */
	public ReadWriteLock getLock();
}
