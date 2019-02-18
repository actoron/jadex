package jadex.tools.web;

import java.util.Collection;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceEvent;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  
 */
@Service(system=true)
public interface IJCCWebService 
{
	/**
	 *  Get the established connections.
	 *  @return A list of connections.
	 */
	public IFuture<Collection<IComponentIdentifier>> getPlatforms();
	
	/**
	 *  Get events about known platforms.
	 *  @return Events for platforms.
	 */
	public ISubscriptionIntermediateFuture<ServiceEvent<IComponentIdentifier>> subscribeToPlatforms();
	
	/**
	 *  Get the JCC plugin html fragments.
	 */
	public IFuture<Map<String, String>> getPluginFragments(IComponentIdentifier cid);
	
	/**
	 *  Invoke a Jadex service on the managed platform.
	 */
	public IFuture<Object> invokeServiceMethod(IComponentIdentifier cid, String servicetype, String methodname, Object[] args);
	
}
