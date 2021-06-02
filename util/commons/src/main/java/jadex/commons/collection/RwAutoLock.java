package jadex.commons.collection;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *  Class wrapping a read-write lock mechanism that can be used
 *  with try-with-resources.
 *
 */
public class RwAutoLock
{
	/** The internal lock. */
	protected ReadWriteLock rwlock;
	
	/** The resource read unlock. */
	protected IAutoLock readunlock;
	
	/** The resource write unlock. */
	protected IAutoLock writeunlock;
	
	/**
	 *  Creates the auto-lock.
	 */
	public RwAutoLock()
	{
		this(new ReentrantReadWriteLock(false));
	}
	
	/**
	 *  Creates the auto-lock.
	 *  @param lock Specific RW-lock to use internally.
	 */
	public RwAutoLock(ReadWriteLock lock)
	{
		rwlock = lock;
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
