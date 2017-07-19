package jadex.micro.examples.microservice;

/**
 * 
 */
//@Microservice
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
