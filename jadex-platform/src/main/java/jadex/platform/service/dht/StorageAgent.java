package jadex.platform.service.dht;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.types.dht.IKVStore;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.CreationInfo;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@ProvidedServices( {
	@ProvidedService(type = IKVStore.class, implementation = @Implementation(value = KVStore.class), scope = RequiredServiceInfo.SCOPE_GLOBAL)
})
@RequiredServices( {
	@RequiredService(name="store", type = IKVStore.class, binding=@Binding(scope=Binding.SCOPE_LOCAL)),
	@RequiredService(name="ring", type = IRingNode.class, binding=@Binding(scope=Binding.SCOPE_LOCAL, create = true,
	creationinfo=@CreationInfo(type = "ringAgent")))
})
@ComponentTypes(@ComponentType(name="ringAgent", clazz=RingAgent.class))
public class StorageAgent
{
//	@AgentService
	IKVStore store;
	
	@AgentService
	IRingNode ring;
	
	@Agent
	IInternalAccess agent;
	
	@AgentCreated
	public void onCreate() {
		store = agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(IKVStore.class);
		
		store.setRing(ring);
//		store.publish("test", "0123");
	}
}
