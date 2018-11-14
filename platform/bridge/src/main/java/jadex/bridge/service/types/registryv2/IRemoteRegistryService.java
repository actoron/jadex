package jadex.bridge.service.types.registryv2;

import java.util.Set;

import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Security;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.IFuture;

/**
 *  Service access to a remote registry.
 *  Plain mode does not support queries.
 *  Instead each client is expected to periodically search remotely to handle its queries.
 *  
 *  For remote queries use the extended ISuperpeerService, if available.
 */
@Service(system=true)
@Security(roles=Security.UNRESTRICTED)	// Allow invocation and check in impl.
public interface IRemoteRegistryService
{
	/** Name of the remote registry component and service. */
	public static final String REMOTE_REGISTRY_NAME = "remoteregistry";
	
	/**
	 *  Search remote registry for a single service.
	 *  
	 *  @param query The search query.
	 *  @return The first matching service or null if not found.
	 */
	public IFuture<IServiceIdentifier> searchService(ServiceQuery<?> query);
	
	/**
	 *  Search remote registry for services.
	 *  
	 *  @param query The search query.
	 *  @return The matching services or empty set if none are found.
	 */
	public IFuture<Set<IServiceIdentifier>> searchServices(ServiceQuery<?> query);
}
