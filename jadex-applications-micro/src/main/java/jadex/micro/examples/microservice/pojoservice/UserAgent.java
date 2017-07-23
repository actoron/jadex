package jadex.micro.examples.microservice.pojoservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

@Agent
public class UserAgent
{	
	@Agent
	protected IInternalAccess agent;

	@AgentBody
	public void body()
	{
		PojoMicroservice ser = SServiceProvider.getService(agent, PojoMicroservice.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		System.out.println(ser.sayHello("Lars"));
	}
}
