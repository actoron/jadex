package jadex.micro.servicecall;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent providing a raw service.
 */
@ProvidedServices(@ProvidedService(type=IServiceCallService.class,
	implementation=@Implementation(expression="new RawServiceCallService($component.getComponentIdentifier())",
		proxytype=Implementation.PROXYTYPE_RAW)))
@Agent
public class RawServiceAgent
{
}
