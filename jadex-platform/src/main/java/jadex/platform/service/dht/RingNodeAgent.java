package jadex.platform.service.dht;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.dht.IRingApplicationService;
import jadex.bridge.service.types.dht.IRingNodeDebugService;
import jadex.bridge.service.types.dht.IRingNodeService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * This Agent represents a part of the DHT ring, providing a Ring Node Service.
 */
@Agent
@ProvidedServices({
	@ProvidedService(name="providedRing", type = IRingNodeService.class, implementation = @Implementation(RingNodeService.class), scope = RequiredServiceInfo.SCOPE_GLOBAL),
	@ProvidedService(name="providedRingApplication", type = IRingApplicationService.class, implementation = @Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\"providedRing\")"), scope = RequiredServiceInfo.SCOPE_GLOBAL),
	@ProvidedService(name="providedDebugRing", type = IRingNodeDebugService.class, implementation = @Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\"providedRing\")"), scope = RequiredServiceInfo.SCOPE_GLOBAL),
})
@RequiredServices({
	@RequiredService(name = "ringnodes", type = IRingNodeService.class, multiple = true, binding=@Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL, dynamic = true))
})
public class RingNodeAgent 
{
}
