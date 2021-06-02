package jadex.microservice.examples.pojoservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Micro agent that uses the pojo service.
 *  Search is here done by using the implementation class not the interface.
 */
@Agent
public class UserAgent
{	
	/**
	 *  The agent body is called once on agent startup.
	 */
	//@AgentBody
	@OnStart
	public void body(IInternalAccess agent)
	{
		try
		{
			PojoMicroservice ser = agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(PojoMicroservice.class, ServiceScope.PLATFORM)).get();
			System.out.println(ser.sayHello("Lars"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
