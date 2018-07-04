package jadex.launch.test.servicecall;

import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent providing a direct service.
 */
@RequiredServices({
	@RequiredService(name="raw", type=IServiceCallService.class, binding=@Binding(proxytype=Binding.PROXYTYPE_RAW, scope=Binding.SCOPE_GLOBAL)),
	@RequiredService(name="direct", type=IServiceCallService.class, binding=@Binding(proxytype=Binding.PROXYTYPE_DIRECT, scope=Binding.SCOPE_GLOBAL)),
	@RequiredService(name="decoupled", type=IServiceCallService.class, binding=@Binding(proxytype=Binding.PROXYTYPE_DECOUPLED, scope=Binding.SCOPE_GLOBAL)),
	@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
})
@Agent
public class ServiceCallAgent
{
}
