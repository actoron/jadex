package jadex.micro.examples.microservice.pojoservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;

@Service
public class UserMicroservice
{
	@ServiceComponent
	protected IInternalAccess agent;
	
	@ServiceStart
//	public void start(IInternalAccess agent)
	public void start()
	{
		PojoMicroservice ser = SServiceProvider.getService(agent, PojoMicroservice.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		System.out.println(ser.sayHello("Lars"));
	}
}
