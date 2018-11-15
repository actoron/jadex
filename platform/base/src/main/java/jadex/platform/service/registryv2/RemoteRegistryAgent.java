package jadex.platform.service.registryv2;

import java.util.Collections;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ServiceIdentifier;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.registryv2.IRemoteRegistryService;
import jadex.bridge.service.types.security.ISecurityInfo;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Plain service access to a remote registry.
 *  See SuperpeerRegistryAgent for extended implementation supporting also persistent queries.
 */
@Service
@Agent(name=IRemoteRegistryService.REMOTE_REGISTRY_NAME, autostart=Boolean3.TRUE)
@ProvidedServices(@ProvidedService(type=IRemoteRegistryService.class, name=IRemoteRegistryService.REMOTE_REGISTRY_NAME, scope=ServiceScope.NETWORK))
public class RemoteRegistryAgent implements IRemoteRegistryService
{
	
	
	/** Component access. */
	@Agent
	protected IInternalAccess ia;
	
	/** The local service registry. */
	protected ServiceRegistry serviceregistry;
	
	protected IComponentIdentifier platformid;
	
	/**
	 *  Service initialization.
	 *  
	 *  @return Null, when done.
	 */
	@ServiceStart
	public IFuture<Void> start()
	{
		serviceregistry = (ServiceRegistry) ServiceRegistry.getRegistry(ia);
		platformid = ia.getId().getRoot();
		return IFuture.DONE;
	}
	
	/**
	 *  Search remote registry for a single service.
	 *  
	 *  @param query The search query.
	 *  @return The first matching service or null if not found.
	 */
	public IFuture<IServiceIdentifier> searchService(ServiceQuery<?> query)
	{
		checkSecurity(query);
		
		IServiceIdentifier ret = null;
		
		// Scope check why?
//		boolean localowner = query.getOwner().getRoot().equals(platformid);
//		if (!RequiredServiceInfo.isScopeOnLocalPlatform(query.getScope()) || localowner)
		{
			ret = serviceregistry.searchService(query);
		}
		
		return new Future<>(ret);
	}

	/**
	 *  Search remote registry for services.
	 *  
	 *  @param query The search query.
	 *  @return The matching services or empty set if none are found.
	 */
	public IFuture<Set<IServiceIdentifier>> searchServices(ServiceQuery<?> query)
	{
		checkSecurity(query);
		
		Set<IServiceIdentifier> ret = Collections.emptySet();
		
		// Scope check why?
//		boolean localowner = query.getOwner().getRoot().equals(platformid);
//		if (!RequiredServiceInfo.isScopeOnLocalPlatform(query.getScope()) || localowner)
		{
			ret = serviceregistry.searchServices(query);
		}
		
		return new Future<>(ret);
	}

	//-------- helper methods --------
	
	/**
	 *  Check if a query is allowed by caller.
	 *  @throws SecurityExcpetion if not allowed.
	 */
	// TODO: change query to unrestricted instead and always succeed by returning allowed subset of results. 
	protected void checkSecurity(ServiceQuery<?> query)
	{
		boolean allowed	= true;
		
		ISecurityInfo	secinfos	= (ISecurityInfo)ServiceCall.getCurrentInvocation().getProperty(ServiceCall.SECURITY_INFOS);
		if(secinfos==null || !secinfos.hasDefaultAuthorization())
		{
			allowed	= false;
			if(query.getServiceType()!=null)
			{
				Class<?>	type	= query.getServiceType().getType(ia.getClassLoader());
				Security	level	= type.getAnnotation(Security.class);
				if(level!=null)
				{
					Set<String>	roles	= ServiceIdentifier.getRoles(level, ia);
					allowed	= roles.contains(Security.UNRESTRICTED);
				}
			}
		}
		
		if(!allowed)
		{
			throw new SecurityException("Search not allowed: "+query);
		}
	}
}
