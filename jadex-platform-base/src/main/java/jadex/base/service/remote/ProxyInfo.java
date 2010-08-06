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
public class ProxyInfo
{
	//-------- attributes --------
	
	/** The rms. */
	protected IComponentIdentifier rms;
	
	/** The service identifier. */
	protected IServiceIdentifier sid; 
	
	/** The value cache. */
	protected Map cache;
	
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
}
