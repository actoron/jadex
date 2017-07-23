package jadex.micro.examples.microservice.async;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
//@Microservice
public class AsyncMicroservice implements IAsyncService
{
	/**
	 *  Say hello method.
	 *  @param name The name.
	 */
	public IFuture<String> sayHello(String name)
	{
		return new Future<String>("Hello "+name);
	}
}
