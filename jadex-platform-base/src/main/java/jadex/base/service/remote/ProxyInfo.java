package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.service.IServiceIdentifier;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Info struct that holds all necessary information to generate
 *  a proxy on the local platform. Is necessary because a proxy
 *  cannot be directly created on the remote side and then sent 
 *  per message to the calling side.
 */
public class ProxyInfo
{
	//-------- attributes --------
	
	/** The rms. */
	protected IComponentIdentifier rms;
	
	/** The service identifier. */
	protected IServiceIdentifier sid; 
	
	/** The value cache. */
	protected Map cache;
	
	/** The excluded methods. */
	protected Set excluded;
	
	/** The uncached methods. */
	protected Set uncached;

	/** The  methods. */
	protected Set synchronous;
	
	// alternaltively to sid we may have cid and targetclass
	
	/** The component identifier. */
	protected IComponentIdentifier cid; 
	
	/** The target class. */
	protected Class targetclass;
	
	//-------- constructors --------
	
	/**
	 *  Create a new proxy info.
	 */
	public ProxyInfo()
	{
	}

	/**
	 *  Create a new proxy info.
	 */
	public ProxyInfo(IComponentIdentifier rms, IServiceIdentifier sid)
	{
		this.rms = rms;
		this.sid = sid;
	}
	
	/**
	 *  Create a new proxy info.
	 */
	public ProxyInfo(IComponentIdentifier rms, IComponentIdentifier cid, Class targetclass)
	{
		this.rms = rms;
		this.cid = cid;
		this.targetclass = targetclass;
	}

	//-------- methods --------
	
	/**
	 *  Get the rms.
	 *  @return the rms.
	 */
	public IComponentIdentifier getRemoteManagementServiceIdentifier()
	{
		return rms;
	}

	/**
	 *  Set the rms.
	 *  @param rms The rms to set.
	 */
	public void setRemoteManagementServiceIdentifier(IComponentIdentifier rms)
	{
		this.rms = rms;
	}

	/**
	 *  Get the sid.
	 *  @return the sid.
	 */
	public IServiceIdentifier getServiceIdentifier()
	{
		return sid;
	}

	/**
	 *  Set the sid.
	 *  @param sid The sid to set.
	 */
	public void setServiceIdentifier(IServiceIdentifier sid)
	{
		this.sid = sid;
	}
	
	/**
	 *  Get the cached values.
	 *  @return The cached values. 
	 */
	public Map getCache()
	{
		return cache;
	}
	
	/**
	 *  Set the cached values.
	 *  @param cache The cached values. 
	 */
	public void setCache(Map cache)
	{
		this.cache = cache;
	}
	
	/**
	 *  Get the cached values.
	 *  @return The cached values. 
	 */
	public void putCache(Object key, Object value)
	{
		if(cache==null)
			cache = new HashMap();
		cache.put(key, value);
	}
	
	/**
	 *  Get the cid.
	 *  @return the cid.
	 */
	public IComponentIdentifier getComponentIdentifier()
	{
		return cid;
	}

	/**
	 *  Set the cid.
	 *  @param cid The cid to set.
	 */
	public void setComponentIdentifier(IComponentIdentifier cid)
	{
		this.cid = cid;
	}

	/**
	 *  Get the targetclass.
	 *  @return the targetclass.
	 */
	public Class getTargetClass()
	{
		return targetclass;
	}

	/**
	 *  Set the targetclass.
	 *  @param targetclass The targetclass to set.
	 */
	public void setTargetClass(Class targetclass)
	{
		this.targetclass = targetclass;
	}

	/**
	 *  Get the excluded.
	 *  @return the excluded.
	 */
	public Set getExcludedMethods()
	{
		return excluded;
	}

	/**
	 *  Set the excluded.
	 *  @param excluded The excluded to set.
	 */
	public void setExcludedMethods(Set excluded)
	{
		this.excluded = excluded;
	}
	
	/**
	 *  Add an excluded method.
	 *  @param Method excluded.
	 */
	public void addExcludedMethod(MethodInfo m)
	{
		if(excluded==null)
			excluded = new HashSet();
		excluded.add(m);
	}
	
	/**
	 *  Test if method is excluded.
	 *  @param m Method to test.
	 *  @return True, if is excluded.
	 */
	public boolean isExcluded(Method m)
	{
		return excluded!=null && excluded.contains(new MethodInfo(m));
	}

	/**
	 *  Get the uncached.
	 *  @return the uncached.
	 */
	public Set getUncachedMethods()
	{
		return uncached;
	}

	/**
	 *  Set the uncached.
	 *  @param uncached The uncached to set.
	 */
	public void setUncachedMethods(Set uncached)
	{
		this.uncached = uncached;
	}
	
	/**
	 *  Add an uncached method.
	 *  @param Method excluded.
	 */
	public void addUncachedMethod(MethodInfo m)
	{
		if(uncached==null)
			uncached = new HashSet();
		uncached.add(m);
	}
	
	/**
	 *  Test if method is uncached.
	 *  @param m Method to test.
	 *  @return True, if is uncached.
	 */
	public boolean isUncached(Method m)
	{
		return uncached!=null && uncached.contains(new MethodInfo(m));
	}

	/**
	 *  Get the synchronous.
	 *  @return the synchronous.
	 */
	public Set getSynchronousMethods()
	{
		return synchronous;
	}

	/**
	 *  Set the synchronous.
	 *  @param synchronous The synchronous to set.
	 */
	public void setSynchronousMethods(Set synchronous)
	{
		this.synchronous = synchronous;
	}
	
	/**
	 *  Add an synchronous method.
	 *  @param Method excluded.
	 */
	public void addSynchronousMethod(MethodInfo m)
	{
		if(synchronous==null)
			synchronous = new HashSet();
		synchronous.add(m);
	}
	
	/**
	 *  Test if method is synchronous.
	 *  @param m Method to test.
	 *  @return True, if is synchronous.
	 */
	public boolean isSynchronous(Method m)
	{
		return synchronous!=null && synchronous.contains(new MethodInfo(m));
	}
}
