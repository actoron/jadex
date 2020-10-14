package jadex.tools.web.jcc;

import java.util.Collection;
import java.util.Map;

import javax.ws.rs.QueryParam;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.FutureReturnType;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceEvent;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Interface for the web platform that is used as front controller for
 *  all interactions with other platforms.
 */
@Service(system=true)
@Security(roles=Security.UNRESTRICTED)
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
	 *  @param cid The id of the platform to be managed.
	 */
	public IFuture<Map<String, String>> getPluginFragments(IComponentIdentifier cid);
	
	/**
	 *  Get the JCC plugin infos.
	 *  @param cid The id of the platform to be managed.
	 *  @return The plugin infos.
	 */
	public IFuture<JCCWebPluginInfo[]> getPluginInfos(IComponentIdentifier cid);
	
	/**
	 *  Get the web component fragment for a plugin.
	 *  @param name The plugin name.
	 *  @return The web component fragment.
	 */
	public IFuture<String> getPluginFragment(IServiceIdentifier sid);
	
	/**
	 *  Invoke a Jadex service on the managed platform.
	 */
	public IFuture<Object> invokeServiceMethod(IComponentIdentifier cid, ClassInfo servicetype, 
		String methodname, Object[] args, ClassInfo[] argtypes, @QueryParam("returntype") @FutureReturnType ClassInfo rettype);
	
	/**
	 *  Check if a platform is available.
	 */
	public IFuture<Boolean> isPlatformAvailable(IComponentIdentifier cid);
	
	/**
	 *  Login to the webjcc.
	 *  @param platformpass The platform password.
	 *  @return True if logged in.
	 * /
	public IFuture<Boolean> login(String platformpass);*/
}
