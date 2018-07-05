package jadex.micro.examples.microservice.multi;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.micro.examples.microservice.async.IAsyncService;
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
		ISyncService sser = component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISyncService.class));
		IAsyncService aser = component.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IAsyncService.class));
		System.out.println(sser.sayHello("A")+" "+aser.sayMeHello("B").get());
		System.out.println(sser.sayHello("C")+" "+aser.sayMeHello("D").get());
	}
}
