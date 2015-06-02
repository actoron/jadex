package jadex.platform.service.dht;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.types.dht.IDistributedKVStoreService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * Agent that includes a Ring component and provides a storage service additionally. 
 */
@Agent
@Configurations({
	@Configuration(name = "default", arguments={
		@NameValue(name="overlayId", value="\"KVStore\"")
	})
})
@ProvidedServices( {
	@ProvidedService(type = IDistributedKVStoreService.class, implementation = @Implementation(value = DistributedKVStoreService.class), scope = RequiredServiceInfo.SCOPE_GLOBAL)
})
public class DistributedKVStoreAgent extends RingNodeAgent
{
	/** The local store service **/
	private IDistributedKVStoreService store;

	/** The agent access **/
	@Agent
	private IInternalAccess agent;
	
	@AgentCreated
	public void onCreate() {
		store = agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(IDistributedKVStoreService.class);
		store.setRingService(ring);
	}
}
