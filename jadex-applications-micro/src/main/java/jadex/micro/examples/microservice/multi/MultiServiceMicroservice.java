package jadex.micro.examples.microservice.multi;


import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.examples.microservice.async.IAsyncService;
import jadex.micro.examples.microservice.sync.ISyncService;

/**
 *  This example shows a microservice as Java pojo
 */
public class MultiServiceMicroservice implements ISyncService, IAsyncService
{
	protected int cnt;
	
	/**
	 *  Say hello. 
	 *  @param name The name.
	 *  @return The greeting.
	 */
	public String sayHello(String name)
	{
		return "#"+cnt+++": Hello "+name;
	}
	
	/**
	 *  Say hello method.
	 *  @param name The name.
	 */
	public IFuture<String> sayMeHello(String name)
	{
		return new Future<String>("#"+cnt+++": Hello "+name);
	}
}
