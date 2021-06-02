package jadex.platform.servicecall;

import jadex.bridge.service.ServiceScope;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent providing a direct service.
 */
@ProvidedServices(@ProvidedService(type=IServiceCallService.class,
	implementation=@Implementation(value=ServiceCallService.class,
		proxytype=Implementation.PROXYTYPE_DIRECT),
		scope=ServiceScope.GLOBAL))
@Agent
public class DirectServiceAgent
{
}
