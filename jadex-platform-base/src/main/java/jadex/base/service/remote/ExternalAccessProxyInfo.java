package jadex.base.service.remote;

import jadex.bridge.IComponentIdentifier;
import jadex.service.IServiceIdentifier;

import java.util.HashMap;
import java.util.Map;

/**
 *  Info struct that holds all necessary information to generate
 *  a proxy on the local platform. Is necessary because a proxy
 *  cannot be directly created on the remote side and then sent 
 *  per message to the calling side.
 */
public class ExternalAccessProxyInfo
{
	//-------- attributes --------
	
	/** The rms. */
	protected IComponentIdentifier rms;
	
	/** The component identifier. */
	protected IComponentIdentifier cid; 
	
	/** The target class. */
	protected Class targetclass;
	
	/** The value cache. */
	protected Map cache;
	
	//-------- constructors --------
	
	/**
	 *  Create a new proxy info.
	 */
	public ExternalAccessProxyInfo()
	{
	}

	/**
	 *  Create a new proxy info.
	 */
	public ExternalAccessProxyInfo(IComponentIdentifier rms, IComponentIdentifier cid, Class targetclass)
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
}
