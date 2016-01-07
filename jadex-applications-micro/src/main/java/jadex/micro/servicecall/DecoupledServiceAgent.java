package jadex.micro.servicecall;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent providing a decoupled service.
 */
@ProvidedServices(@ProvidedService(type=IServiceCallService.class,
	implementation=@Implementation(value=ServiceCallService.class,
		proxytype=Implementation.PROXYTYPE_DECOUPLED)))
@Agent
public class DecoupledServiceAgent
{
}
