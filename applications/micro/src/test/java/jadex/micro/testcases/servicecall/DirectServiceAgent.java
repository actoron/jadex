package jadex.micro.testcases.servicecall;

import jadex.bridge.sensor.service.TagProperty;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent providing a direct service.
 */
@Arguments(@Argument(name=TagProperty.NAME, clazz=String.class, defaultvalue="\"direct\""))
@ProvidedServices(@ProvidedService(type=IServiceCallService.class,
	implementation=@Implementation(value=ServiceCallService.class,
		proxytype=Implementation.PROXYTYPE_DIRECT)))
@Agent
public class DirectServiceAgent
{
}
