package jadex.platform.service.dht;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.types.dht.IDistributedKVStoreService;
import jadex.bridge.service.types.dht.IRingApplicationService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.CreationInfo;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * Agent that includes a Ring component and provides a storage service additionally. 
 */
@Agent
@Configurations({
	@Configuration(name = "default", components={
		@Component(type="ringAgent", arguments={
			@NameValue(name="overlayId", value="\"KVStore\"")
		})
	})
})
@ComponentTypes({
	@ComponentType(name="ringAgent", clazz=RingNodeAgent.class)
})
@ProvidedServices( {
	@ProvidedService(type = IDistributedKVStoreService.class, implementation = @Implementation(value = DistributedKVStoreService.class), scope = RequiredServiceInfo.SCOPE_GLOBAL)
})
@RequiredServices( {
	@RequiredService(name="ring", type = IRingApplicationService.class, binding=@Binding(scope=Binding.SCOPE_COMPONENT, create = false,
		creationinfo=@CreationInfo(type = "ringAgent")))
})
public class DistributedKVStoreAgent
{
	/** The local store service **/
	private IDistributedKVStoreService store;
	
	/** The local ringnode service **/
	@AgentService
	private IRingApplicationService ring;

	/** The agent access **/
	@Agent
	private IInternalAccess agent;
	
	@AgentCreated
	public void onCreate() {
		store = agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(IDistributedKVStoreService.class);
		store.setRingService(ring);
	}
}
