package jadex.bridge.service;

import jadex.commons.IRemoteFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.lang.reflect.Proxy;
import java.rmi.server.RemoteObjectInvocationHandler;

/**
 *  Test if a class is a proxy.
 */
public class ProxyFilter implements IRemoteFilter
{
	//-------- attributes --------
	
	/** Static proxy filter instance. */
	public static IRemoteFilter PROXYFILTER = new ProxyFilter();

	//-------- methods --------
	
	/**
	 *  Test if service is a proxy.
	 */
	public IFuture filter(Object obj)
	{
		return new Future(!Proxy.isProxyClass(obj.getClass()) || 
			!(Proxy.getInvocationHandler(obj) instanceof RemoteObjectInvocationHandler));
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
