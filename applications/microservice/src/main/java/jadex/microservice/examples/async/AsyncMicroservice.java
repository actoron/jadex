package jadex.microservice.examples.async;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.microservice.annotation.Microservice;

/**
 *  Microservice example with asynchronous interface.
 */
@Microservice
public class AsyncMicroservice implements IAsyncService
{
	/**
	 *  Say hello method.
	 *  @param name The name.
	 */
	public IFuture<String> sayMeHello(String name)
	{
		return new Future<String>("Hello "+name);
	}
}
