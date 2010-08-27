package jadex.commons.collection;

/**
 *  Cache that provide expiration in case of
 *  a) timetolive is exceeded
 *  b) the max number of data has been reached (lru behaviour)
 */
public class Cache
{
	//-------- constants --------
	
	/** The default time to live time span (5 mins). */
	public static final long DEFAULT_TIME_TO_LIVE = 5 * 60 * 1000;
	
	//-------- attributes --------
	
	/** The lru. */
	protected LRU lru;
	
	/** The time to live. */
	protected long ttl;
	
	//-------- constructors --------
	
	/**
	 *  Create a new cache.
	 */
	public Cache(int max)
	{
		this(max, -1);
	}
	
	/**
	 *  Create a new cache.
	 */
	public Cache(int max, long ttl)
	{
		this(new LRU(max), ttl);
	}
	
	/**
	 *  Create a new cache.
	 */
	public Cache(LRU lru, long ttl)
	{
		this.lru = lru;
		this.ttl = ttl>0? ttl: DEFAULT_TIME_TO_LIVE;
	}
	
	//-------- methods --------
	
	/**
	 *  Put an entry in the cache.
	 *  @param key The key.
	 *  @param value The value.
	 *  @param now The current time.
	 */
	public void put(Object key, Object value, long now)
	{
		put(key, value, now, this.ttl);
	}
	
	/**
	 *  Put an entry in the cache.
	 *  @param key The key.
	 *  @param value The value.
	 *  @param now The current time (-1 for never expire).
	 */
	public void put(Object key, Object value, long now, long ttl)
	{
		CacheEntry ce = new CacheEntry(value, now, ttl);
		lru.put(key, ce);
	}
	
	/**
	 *  Get data from the cache.
	 *  @param key The key.
	 *  @param now The current time (-1 for never expire).
	 *  @return The cached object.
	 */
	public Object get(Object key, long now)
	{
		Object ret = null;
		
		CacheEntry ce = (CacheEntry)lru.get(key);
		if(ce!=null)
		{
			if(ce.isExpired(now))
			{
//				System.out.println("expired: "+ce.getData()+" "+now+" "+ce.getCacheDate()+" "+ce.getTimeToLive());
				lru.remove(key);
			}
			else
			{
//				System.out.println("not expired: "+ce.getData()+" "+now+" "+ce.getCacheDate()+" "+ce.getTimeToLive());
				ret = ce.getData();
			}
		}
		
		return ret;
	}
	
	/**
	 *  Remove an entry.
	 *  @param key The key.
	 */
	public boolean remove(Object key)
	{
		return lru.remove(key)!=null;
	}
	
	/**
	 *  Test if a key is contained.
	 *  @param key The key.
	 *  @return  True if contained.
	 */
	public boolean containsKey(Object key)
	{
		return lru.containsKey(key);
	}
	
	/**
	 *  Test if an entry can expire.
	 *  @param key The key.
	 *  @return True, if entry can expire.
	 */
	public boolean canExpire(Object key)
	{
		boolean ret = true;
		CacheEntry ce = (CacheEntry)lru.get(key);
		if(ce!=null)
		{
			ret = ce.getCacheDate()!=-1;
		}
		return ret;
	}
	
	/**
	 *  Get the size.
	 *  @return The size.
	 */
	public int size()
	{
		return lru.size();
	}
}
