package jadex.platform.service.dht;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.dht.IDistributedKVStoreDebugService;
import jadex.bridge.service.types.dht.IDistributedKVStoreService;
import jadex.bridge.service.types.dht.IDistributedServiceRegistryService;
import jadex.bridge.service.types.dht.IRingApplicationService;
import jadex.bridge.service.types.dht.IRingNodeDebugService;
import jadex.bridge.service.types.dht.IRingNodeService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * Agent that provides a distributed service registry. 
 */
@Agent
@Configurations(replace=true, value={
	@Configuration(name = "default", arguments={
		@NameValue(name="overlayId", value="\"ServiceRegistry\"")
	})
})

@ProvidedServices(replace=true, value={
	@ProvidedService(name="providedRegistry", type = IDistributedServiceRegistryService.class, implementation = @Implementation(value = DistributedServiceRegistryService.class), scope = RequiredServiceInfo.SCOPE_GLOBAL),
	
	// needed for kvstore:
	@ProvidedService(name="providedKV", type = IDistributedKVStoreService.class, implementation = @Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\"providedRegistry\")"), scope = RequiredServiceInfo.SCOPE_GLOBAL),
	@ProvidedService(type = IDistributedKVStoreDebugService.class, implementation = @Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\"providedKV\")"), scope = RequiredServiceInfo.SCOPE_GLOBAL),
	
	// needed for RingAgent:	
	@ProvidedService(name="providedRing", type = IRingNodeService.class, implementation = @Implementation(expression = "new jadex.platform.service.dht.RingNodeService($args.overlayId)"), scope = RequiredServiceInfo.SCOPE_GLOBAL),
	@ProvidedService(name="ring", type = IRingApplicationService.class, implementation = @Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\"providedRing\")"), scope = RequiredServiceInfo.SCOPE_GLOBAL),
	@ProvidedService(name="providedDebugRing", type = IRingNodeDebugService.class, implementation = @Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\"providedRing\")"), scope = RequiredServiceInfo.SCOPE_GLOBAL),
})

@RequiredServices({
	@RequiredService(name="registry", type = IDistributedServiceRegistryService.class, binding=@Binding(scope=Binding.SCOPE_COMPONENT, create = false)),
//	@RequiredService(name="store", type = IDistributedKVStoreService.class, binding=@Binding(scope=Binding.SCOPE_COMPONENT, create = false))
})
public class DistributedServiceRegistryAgent extends DistributedKVStoreAgent
{
}
