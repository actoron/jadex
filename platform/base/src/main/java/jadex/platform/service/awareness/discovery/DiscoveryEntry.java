package jadex.platform.service.awareness.discovery;

import jadex.bridge.service.types.awareness.AwarenessInfo;

/**
 *  Simple data struct for saving discovery info.
 */
public class DiscoveryEntry
{	
	//-------- attributes --------
	
	/** The awareness info. */
	protected AwarenessInfo info;
	
	/** The time. */
	protected long time;
	
	/** The entry. */
	protected Object entry;
	
	//-------- constructors --------
	
	/**
	 *  Create an entry.
	 */
	public DiscoveryEntry(AwarenessInfo info, long time, Object entry)
	{
		this.info = info;
		this.time = time;
		this.entry = entry;
	}
	
	//-------- methods --------
	
	
	/**
	 *  Get the info.
	 *  @return The info.
	 */
	public AwarenessInfo getInfo()
	{
		return info;
	}

	/**
	 *  Set the info.
	 *  @param info The info to set.
	 */
	public void setInfo(AwarenessInfo info)
	{
		this.info = info;
	}
	
	/**
	 *  Get the time.
	 *  @return The time.
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
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "DiscoveryEntry(cid=" + info.getSender() + ", master=" + info.getMasterId() + ")";
	}
}
