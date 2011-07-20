package jadex.base.service.awareness.discovery;

import jadex.bridge.IComponentIdentifier;

/**
 *  Simple data struct for saving discovery info.
 */
public class DiscoveryEntry
{	
	//-------- attributes --------
	
	/** The component identifier of the remote component. */
	public IComponentIdentifier cid;

	/** Time when last awareness info was received. */
	public long time;

	/** The current send delay time. */
	public long delay;
	
	/** The entry. */
	public Object entry;
	
	/** Is master. */
	public boolean master;
	
	//-------- constructors --------
	
	/**
	 *  Create an entry.
	 */
	public DiscoveryEntry(IComponentIdentifier cid, long time, long delay, Object entry, boolean master)
	{
		this.cid = cid;
		this.time = time;
		this.delay = delay;
		this.entry = entry;
		this.master = master;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the time.
	 *  @return the time.
	 */
	public long getTime()
	{
		return time;
	}

	/**
	 *  Set the time.
	 *  @param time The time to set.
	 */
	public void setTime(long time)
	{
		this.time = time;
	}
	
	/**
	 *  Get the delay.
	 *  @return the delay.
	 */
	public long getDelay()
	{
		return delay;
	}

	/**
	 *  Set the delay.
	 *  @param delay The delay to set.
	 */
	public void setDelay(long delay)
	{
		this.delay = delay;
	}

	/**
	 *  Get the entry.
	 *  @return the entry.
	 */
	public Object getEntry()
	{
		return entry;
	}

	/**
	 *  Set the entry.
	 *  @param entry The entry to set.
	 */
	public void setEntry(Object entry)
	{
		this.entry = entry;
	}
	
	/**
	 *  Get the component identifier.
	 *  @return the component identifier.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return cid;
	}

	/**
	 *  Set the component identifier.
	 *  @param component identifier The component identifier to set.
	 */
	public void setComponentIdentifier(IComponentIdentifier componentIdentifier)
	{
		this.cid = componentIdentifier;
	}

	/**
	 *  Get the master.
	 *  @return the master.
	 */
	public boolean isMaster()
	{
		return master;
	}

	/**
	 *  Set the master.
	 *  @param master The master to set.
	 */
	public void setMaster(boolean master)
	{
		this.master = master;
	}
	
}
