package jadex.commons.collection;

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
	 *  Gets the read lock for manual locking.
	 */
	public Lock readLock();
	
	/**
	 *  Gets the write lock for manual locking.
	 */
	public Lock writeLock();
	
	/**
	 *  Gets the internal lock.
	 *  @return The lock.
	 */
	public ReadWriteLock getLock();
}
