package jadex.platform.service.remote;

import java.util.HashMap;
import java.util.Map;

/**
 *  A proxy reference is the transfer format of a remote reference which should
 *  be made to a proxy on the other side.
 */
public class ProxyReference
{
	//-------- attributes --------
	
	/** The proxy info. */
	protected ProxyInfo pi;
	
	/** The remote reference. */
	protected RemoteReference rr;
	
	/** The value cache. */
	protected Map cache;
		
	//-------- constructors --------
	
	/**
	 *  Create a new proxy reference.
	 */
	public ProxyReference()
	{
		super();
	}

	/**
	 *  Create a new proxy reference.
	 */
	public ProxyReference(ProxyInfo pi, RemoteReference rr)
	{
		this.pi = pi;
		this.rr = rr;
	}

	//-------- methods --------
	
	/**
	 *  Get the proxy info.
	 *  @return The proxy info.
	 */
	public ProxyInfo getProxyInfo()
	{
		return pi;
	}

	/**
	 *  Set the pi.
	 *  @param pi The pi to set.
	 */
	public void setProxyInfo(ProxyInfo pi)
	{
		this.pi = pi;
	}

	/**
	 *  Get the remoteReference.
	 *  @return the remoteReference.
	 */
	public RemoteReference getRemoteReference()
	{
		return rr;
	}

	/**
	 *  Set the remote reference.
	 *  @param remote reference The remote reference to set.
	 */
	public void setRemoteReference(RemoteReference remoteReference)
	{
		this.rr = remoteReference;
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
	 */
	public void putCache(Object key, Object value)
	{
		if(cache==null)
			cache = new HashMap();
		cache.put(key, value);
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ProxyReference(reference="+rr+", cache="+cache+", info="+pi+")";
	}
}
