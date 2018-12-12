package jadex.microservice.examples.sync;

import jadex.microservice.annotation.Microservice;

/**
 *  Microservice example with synchronous interface.
 */
@Microservice
public class SyncMicroservice implements ISyncService
{
	/**
	 *  Say hello method.
	 *  @param name The name.
	 */
	public String sayHello(String name)
	{
		return "Hello "+name;
	}
}
