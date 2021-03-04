package jadex.micro.testcases.servicecall;

import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.ServiceScope;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent providing a decoupled service.
 */
@Arguments(@Argument(name=TagProperty.NAME, clazz=String.class, defaultvalue="\"decoupled\""))
@ProvidedServices(@ProvidedService(type=IServiceCallService.class, scope=ServiceScope.GLOBAL,
	implementation=@Implementation(value=ServiceCallService.class,
		proxytype=Implementation.PROXYTYPE_DECOUPLED)))
@Agent
public class DecoupledServiceAgent
{
}
