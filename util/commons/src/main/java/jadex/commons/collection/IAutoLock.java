package jadex.commons.collection;

public interface IAutoLock extends AutoCloseable
{
	/**
	 *  Manually releases the lock.
	 *  WARNING: Do not combine with try-with-resource
	 *  to avoid duplicate unlocking.
	 */
	public void release();
	
	/**
	 *  Unlocks the lock via the resource approach.
	 */
	public void close();
}
