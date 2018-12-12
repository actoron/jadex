package jadex.microservice.examples.helloworld;

import jadex.bridge.service.annotation.Service;

/**
 *  Say hello service.
 */
@Service // Mark the interface as service interface. Important for service search
public interface IHelloService
{
	/**
	 *  Say hello method.
	 *  @param name The name to greet.
	 *  @return The greeting.
	 */
	public String sayHello(String name);
}
