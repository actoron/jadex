package jadex.platform.servicecall;

import jadex.bridge.service.ServiceScope;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent providing a direct service.
 */
@RequiredServices({
	@RequiredService(name="raw", type=IServiceCallService.class, proxytype=RequiredService.PROXYTYPE_RAW, scope=ServiceScope.GLOBAL),
	@RequiredService(name="direct", type=IServiceCallService.class, proxytype=RequiredService.PROXYTYPE_DIRECT, scope=ServiceScope.GLOBAL),
	@RequiredService(name="decoupled", type=IServiceCallService.class, proxytype=RequiredService.PROXYTYPE_DECOUPLED, scope=ServiceScope.GLOBAL),
})
@Agent
public class ServiceCallAgent
{
}
	