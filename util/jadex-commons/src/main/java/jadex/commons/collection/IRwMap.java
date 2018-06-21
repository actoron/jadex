package jadex.commons.collection;

import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

/**
 *  Interface for thread-safe maps using a read-write lock.
 *
 */
public interface IRwMap<K, V> extends Map<K, V>
{
	/**
	 *  Gets the read lock for manual locking.
	 */
	public ReadLock readLock();
	
	/**
	 *  Gets the write lock for manual locking.
	 */
	public WriteLock writeLock();
}
