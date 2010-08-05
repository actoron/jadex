package jadex.service;

import jadex.commons.IFilter;

import java.lang.reflect.Proxy;

/**
 *  Test if a class is a proxy.
 */
public class ProxyFilter implements IFilter
{
	/** Static proxy filter instance. */
	public static IFilter PROXYFILTER = new ProxyFilter();

	/**
	 *  Test if service is a proxy.
	 */
	public boolean filter(Object obj)
	{
		return !Proxy.isProxyClass(obj.getClass());
	}
	
	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return getClass().hashCode();
	}

	/**
	 *  Test if an object is equal to this.
	 */
	public boolean equals(Object obj)
	{
		return obj!=null && obj.getClass().equals(this.getClass());
	}
}
