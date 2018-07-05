package jadex.launch.test.servicecall;

import jadex.bridge.service.RequiredServiceInfo;
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
			implementation=@Implementation(expression="new RawServiceCallService($component.getIdentifier())",
			proxytype=Implementation.PROXYTYPE_RAW),
			scope=RequiredServiceInfo.SCOPE_GLOBAL
		)
)
@Agent
public class RawServiceAgent
{
}
