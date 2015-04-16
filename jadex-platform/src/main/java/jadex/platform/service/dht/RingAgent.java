package jadex.platform.service.dht;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.dht.IRingNode;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Agent
@ProvidedServices({
	@ProvidedService(type = IRingNode.class, implementation = @Implementation(value = RingNode.class), scope = RequiredServiceInfo.SCOPE_GLOBAL),
})
@RequiredServices({
	@RequiredService(name = "ringnodes", type = IRingNode.class, multiple = true, binding=@Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL, dynamic = true))
})
public class RingAgent
{

}
