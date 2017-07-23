package jadex.micro.examples.microservice.pojoservice;

import jadex.bridge.service.annotation.Service;

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
