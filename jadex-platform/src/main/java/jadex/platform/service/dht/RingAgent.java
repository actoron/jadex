package jadex.platform.service.dht;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.dht.IDebugRingNode;
import jadex.bridge.service.types.dht.IRingNode;
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
	@ProvidedService(name="providedRing", type = IRingNode.class, implementation = @Implementation(RingNode.class), scope = RequiredServiceInfo.SCOPE_GLOBAL),
	@ProvidedService(name="providedDebugRing", type = IDebugRingNode.class, implementation = @Implementation(expression="$component.getComponentFeature(jadex.bridge.service.component.IProvidedServicesFeature.class).getProvidedServiceRawImpl(\"providedRing\")"), scope = RequiredServiceInfo.SCOPE_GLOBAL),
})
@RequiredServices({
	@RequiredService(name = "ringnodes", type = IRingNode.class, multiple = true, binding=@Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL, dynamic = true))
})
public class RingAgent 
{
}
