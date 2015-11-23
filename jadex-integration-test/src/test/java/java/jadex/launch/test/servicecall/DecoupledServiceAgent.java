package jadex.launch.test.servicecall;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent providing a decoupled service.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IServiceCallService.class,
	implementation=@Implementation(value=ServiceCallService.class,
		proxytype=Implementation.PROXYTYPE_DECOUPLED), scope = RequiredServiceInfo.SCOPE_GLOBAL))
public class DecoupledServiceAgent
{
	
	@AgentCreated
	public void created() {
		System.out.println("created");
	}
}
