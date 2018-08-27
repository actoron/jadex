package jadex.bridge.service.types.cms;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.Future;

/**
 *  Entry that represents a lock for a component.
 *  Is used to lock the parent while a child is created.
 */
public class LockEntry
{
	//-------- attributes --------
	
	/** The locked component. */
	protected IComponentIdentifier locked;
	
	/** The components that have a lock. */
	protected Set<String> lockers;
	
	/** The kill flag. */
	protected Future<Map<String, Object>> killfuture;
	
	//-------- constructors --------
	
	/**
	 *  Create a new lock entry.
	 */
	public LockEntry(IComponentIdentifier locked)
	{
		this.locked = locked;
	}
	
	//-------- methods --------
	
	/**
	 *  Add a locker id.
	 *  @param locker The locker id.
	 */
	public void addLocker(String locker)
	{
		if(lockers==null)
			lockers = new HashSet<String>();
		lockers.add(locker);
	}
	
	/**
	 *  Remove a locker id.
	 *  @param locker The locker id.
	 *  @return True, if it was last lock and the component needs to be killed.
	 */
	public boolean removeLocker(String locker)
	{
		lockers.remove(locker);
		return lockers.isEmpty() && killfuture!=null;
//			destroyComponent(locked, killfuture);
	}
	
	/**
	 *  Get the locker count.
	 *  @return The number of lockers.
	 */
	public int getLockerCount()
	{
		return lockers==null? 0: lockers.size();
	}

	/**
	 *  Get the killfuture.
	 *  @return the killfuture.
	 */
	public Future<Map<String, Object>> getKillFuture()
	{
		return killfuture;
	}

	/**
	 *  Set the killfuture.
	 *  @param killfuture The killfuture to set.
	 */
	public void setKillFuture(Future<Map<String, Object>> killfuture)
	{
		this.killfuture = killfuture;
	}

	/**
	 * @return the locked
	 */
	public IComponentIdentifier getLocked()
	{
		return locked;
	}
}