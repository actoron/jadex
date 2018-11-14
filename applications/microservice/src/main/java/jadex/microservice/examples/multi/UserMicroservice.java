package jadex.microservice.examples.multi;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.microservice.annotation.Microservice;
import jadex.microservice.examples.async.IAsyncService;
import jadex.microservice.examples.sync.ISyncService;

/**
 *  An example microservice user.
 *  Searches another service and invokes it.
 */
@Microservice
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
		ISyncService sser = component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISyncService.class, ServiceScope.PLATFORM));
		IAsyncService aser = component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IAsyncService.class, ServiceScope.PLATFORM));
		System.out.println(sser.sayHello("A")+" "+aser.sayMeHello("B").get());
		System.out.println(sser.sayHello("C")+" "+aser.sayMeHello("D").get());
	}
}
