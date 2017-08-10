package jadex.micro.examples.microservice.multi;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;
import jadex.micro.examples.microservice.sync.ISyncService;

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
		ISyncService sser = SServiceProvider.getService(component, ISyncService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		ISyncService aser = SServiceProvider.getService(component, ISyncService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		System.out.println(sser.sayHello("A")+" "+aser.sayHello("B"));
		System.out.println(sser.sayHello("C")+" "+aser.sayHello("D"));
	}
}
