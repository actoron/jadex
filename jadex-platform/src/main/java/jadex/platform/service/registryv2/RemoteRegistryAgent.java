package jadex.platform.service.registryv2;

import java.util.Set;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.IServiceRegistry;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceRegistry;
import jadex.bridge.service.types.registryv2.IRemoteRegistryService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/**
 *  Service access to a remote registry.
 */
@Agent
public class RemoteRegistryAgent implements IRemoteRegistryService
{
	/** Component access. */
	@Agent
	protected IInternalAccess ia;
	
	/** The local service registry. */
	protected IServiceRegistry serviceregistry;
	
	/**
	 *  Service initialization.
	 *  
	 *  @return Null, when done.
	 */
	@ServiceStart
	public IFuture<Void> start()
	{
		serviceregistry = ServiceRegistry.getRegistry(ia);
		return IFuture.DONE;
	}
	
	/**
	 *  Search remote registry for a single service.
	 *  
	 *  @param query The search query.
	 *  @return The first matching service or exception if not found.
	 */
	public <T> IFuture<T> searchService(ServiceQuery<T> query)
	{
		return new Future<T>(serviceregistry.searchServiceSync(query));
	}
	
	/**
	 *  Search remote registry for services.
	 *  
	 *  @param query The search query.
	 *  @return The matching services or exception if none are found.
	 */
	public <T> IFuture<Set<T>> searchServices(ServiceQuery<T> query)
	{
		return new Future<Set<T>>(serviceregistry.searchServicesSync(query));
	}
}
