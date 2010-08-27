package jadex.commons.collection;

/**
 *  Entry for cache.
 */
public class CacheEntry
{	
	//-------- attributes --------
	
	/** The cache data. */
	protected Object data;
	
	/** The cache data. */
	protected long cachedate;
	
	/** The time to live. */
	protected long ttl;

	//-------- constructors --------
	
	/**
	 *  Create a new cache entry.
	 */
	public CacheEntry(Object data, long cachedate, long ttl)
	{
		this.data = data;
		this.cachedate = cachedate;
		this.ttl = ttl;
	}

	//-------- methods --------
	
	/**
	 *  Get the data.
	 *  @return the data.
	 */
	public Object getData()
	{
		return data;
	}
	
	/**
	 *  Get the cachedate.
	 *  @return the cachedate.
	 */
	public long getCacheDate()
	{
		return cachedate;
	}
	
	/**
	 *  Get the ttl.
	 *  @return the ttl.
	 */
	public long getTimeToLive()
	{
		return ttl;
	}

	/**
	 *  Test if a cache entry is expired.
	 *  @param now The current time (-1 for never expire).
	 *  @return True, if it is expired.
	 */
	public boolean isExpired(long now)
	{
		return now!=-1 && cachedate!=-1 && cachedate + ttl < now;
	}
}
