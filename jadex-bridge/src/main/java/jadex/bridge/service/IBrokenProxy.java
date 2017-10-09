package jadex.bridge.service;

import jadex.bridge.component.impl.remotecommands.ProxyInfo;

/**
 *  Marker interface for broken proxies.
 *  Proxies are typically broken when not alle target classes are
 *  available on the target component. 
 */
public interface IBrokenProxy
{
	/**
	 *  Get the proxy info of the proxy.
	 *  @return The proxy info (used to create a 
	 */
	public ProxyInfo getProxyInfo();
}
