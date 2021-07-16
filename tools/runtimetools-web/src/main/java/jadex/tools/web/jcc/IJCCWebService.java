package jadex.tools.web.jcc;

import java.util.Collection;
import java.util.Map;

import javax.ws.rs.QueryParam;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.FutureReturnType;
import jadex.bridge.service.annotation.ParameterInfo;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Value;
import jadex.bridge.service.search.ServiceEvent;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.extension.rs.invoke.annotation.ParameterMapper;
import jadex.extension.rs.publish.annotation.ParametersMapper;
import jadex.tools.web.jcc.JCCWebAgent.InvokeServiceMethodMapper;

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
	@ParametersMapper(@Value(clazz=InvokeServiceMethodMapper.class))
	public IFuture<Object> invokeServiceMethod(@ParameterInfo("cid") IComponentIdentifier cid, @ParameterInfo("servicetype") ClassInfo servicetype, 
		@ParameterInfo("methodname") String methodname, @ParameterInfo("args") Object[] args, @ParameterInfo("argtypes") ClassInfo[] argtypes, @QueryParam("returntype") @FutureReturnType ClassInfo rettype);
	
	/**
	 *  Check if a platform is available.
	 */
	public IFuture<Boolean> isPlatformAvailable(IComponentIdentifier cid);
	
	/**
	 *  Get the configuration for web clients.
	 *  
	 *  @return Configuration for web clients.
	 */
	public IFuture<Map<String, Object>> getWebClientConfiguration();
	
	/**
	 *  Login to the webjcc.
	 *  @param platformpass The platform password.
	 *  @return True if logged in.
	 * /
	public IFuture<Boolean> login(String platformpass);*/
}
