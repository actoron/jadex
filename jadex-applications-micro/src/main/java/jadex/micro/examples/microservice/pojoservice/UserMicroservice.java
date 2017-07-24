package jadex.micro.examples.microservice.pojoservice;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;

/**
 *  An example microservice user.
 *  Searches another service and invokes it.
 */
@Service
public class UserMicroservice
{
	/**
	 *  Called on service startup.
	 *  @param component The injected component. 
	 */
	@ServiceStart
	public void start(IInternalAccess component)
	{
		PojoMicroservice ser = SServiceProvider.getService(component, PojoMicroservice.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		System.out.println(ser.sayHello("Lars"));
	}
}
