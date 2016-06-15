package jadex.platform.service.dht;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.dht.IDistributedKVStoreDebugService;
import jadex.bridge.service.types.dht.IDistributedKVStoreService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

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
	@ProvidedService(name="providedKV", type = IDistributedKVStoreService.class, implementation = @Implementation(value = DistributedKVStoreService.class), scope = RequiredServiceInfo.SCOPE_GLOBAL),
	@ProvidedService(type = IDistributedKVStoreDebugService.class, implementation = @Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\"providedKV\")"), scope = RequiredServiceInfo.SCOPE_GLOBAL)
})
@RequiredServices({
	@RequiredService(name="store", type = IDistributedKVStoreService.class, binding=@Binding(scope=Binding.SCOPE_COMPONENT, create = false))
})
public class DistributedKVStoreAgent extends RingNodeAgent
{
	/** The local store service **/
	@AgentService
	private IDistributedKVStoreService store;

	/** The agent access **/
	@Agent
	private IInternalAccess agent;
	
	@AgentCreated
	public void onCreate() {
		super.onCreate();
		store.setRingService(ring);
//		System.out.println("kv store agent created");
		store.setInitialized(true);
	}
}
