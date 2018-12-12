package jadex.microservice.examples.pojoservice;

import jadex.bridge.service.annotation.Service;
import jadex.microservice.annotation.Microservice;

/**
 *  This example shows a microservice as Java pojo
 *  
 *  Note that here no interface is used. 
 */
@Service
@Microservice
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
