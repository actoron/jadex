package jadex.microservice.examples.helloworld;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceIdentifier;
import jadex.microservice.annotation.Microservice;

/**
 *  The hello service impl.
 */
@Microservice // Means that this element is a microservice and can be loaded and started
public class HelloService implements IHelloService
{
	/** Access to the hosting component. */
	@ServiceComponent
	protected IInternalAccess comp;
	
	/** Access to the service id. */
	@ServiceIdentifier
	protected IServiceIdentifier sid;
	
	/**
	 *  Say hello method.
	 *  @param name The name to greet.
	 *  @return The greeting.
	 */
	public String sayHello(String name)
	{
		return "Hello  "+name+" from component "+comp.getId()+" using service "+sid;
	}
}
