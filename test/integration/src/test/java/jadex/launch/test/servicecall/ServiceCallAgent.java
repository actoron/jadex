package jadex.launch.test.servicecall;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent providing a direct service.
 */
@RequiredServices({
	@RequiredService(name="raw", type=IServiceCallService.class, proxytype=RequiredService.PROXYTYPE_RAW, scope=RequiredService.SCOPE_GLOBAL),
	@RequiredService(name="direct", type=IServiceCallService.class, proxytype=RequiredService.PROXYTYPE_DIRECT, scope=RequiredService.SCOPE_GLOBAL),
	@RequiredService(name="decoupled", type=IServiceCallService.class, proxytype=RequiredService.PROXYTYPE_DECOUPLED, scope=RequiredService.SCOPE_GLOBAL),
})
@Agent
public class ServiceCallAgent
{
}
	