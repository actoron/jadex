package jadex.bridge.service;

import jadex.bridge.component.impl.remotecommands.ProxyReference;

/**
 *  Marker interface for broken proxies.
 *  Proxies are typically broken when not alle target classes are
 *  available on the target component. 
 */
public interface IBrokenProxy
{
//	/**
//	 *  Get the proxy info of the proxy.
//	 *  @return The proxy info (used to create a new proxy).
//	 */
//	public ProxyInfo getProxyInfo();
	
	/**
	 *  Get the proxy reference
	 *  @return The proxy reference (used to create a new proxy).
	 */
	public ProxyReference getProxyReference();
}
