package jadex.platform.servicecall;

import jadex.bridge.service.ServiceScope;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent providing a raw service.
 */
@ProvidedServices(
		@ProvidedService(
			type=IServiceCallService.class,
			implementation=@Implementation(expression="new RawServiceCallService($component.getId())",
			proxytype=Implementation.PROXYTYPE_RAW),
			scope=ServiceScope.GLOBAL
		)
)
@Agent
public class RawServiceAgent
{
}
