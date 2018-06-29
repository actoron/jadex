package jadex.micro.examples.microservice.pojoservice;

import jadex.bridge.service.annotation.Service;

/**
 *  This example shows a microservice as Java pojo
 */
@Service
public class PojoMicroservice
{
	/**
	 *  Say hello. 
	 *  @param name The name.
	 *  @return The greeting.
	 */
	public String sayHello(String name)
	{
		return "Hello "+name;
	}
}
