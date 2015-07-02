package jadex.bridge.service.types.dht;

import java.util.Collection;

import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;

/**
 * This Service provides Distributed Key-Value storage using the DHT-based Chord protocol. 
 */
@Reference
public interface IDistributedServiceRegistryService extends IDistributedKVStoreService
{

	/**
	 * Publish a service in the distributed registry.
	 * @param typeName Type of the service (fully-qualified)
	 * @param serviceIdentifier SID of the service
	 * @return Void
	 */
	public IFuture<Void> publish(String typeName, IServiceIdentifier serviceIdentifier);
	
	@Override
	public IFuture<Collection<ServiceRegistration>> lookup(String key, IID idHash);
	
	@Override
	public IFuture<Collection<ServiceRegistration>> lookup(String key);
}
