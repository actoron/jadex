package jadex.microservice.examples.pojoservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
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
	@AgentBody
	public void body(IInternalAccess agent)
	{
		try
		{
			PojoMicroservice ser = agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( PojoMicroservice.class, RequiredServiceInfo.SCOPE_PLATFORM)).get();
			System.out.println(ser.sayHello("Lars"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
