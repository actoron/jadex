package jadex.microservice.examples.helloworld;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.micro.annotation.Agent;

/**
 *  Simple agent that searches and uses the service.
 */
@Agent
public class ServiceUser
{
	/** Access to the agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Executed once after init.
	 */
	//@AgentBody
	@OnStart
	public void body()
	{
		// Search the service.
		IHelloService ser = agent.getFeature(IRequiredServicesFeature.class).getLocalService(new ServiceQuery<>(IHelloService.class, ServiceScope.PLATFORM));
		
		// Invoke and print the result.
		System.out.println(ser.sayHello(agent.getId().getLocalName()));
	}
}
