package jadex.microservice.examples.multi;


import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.microservice.annotation.Microservice;
import jadex.microservice.examples.async.IAsyncService;
import jadex.microservice.examples.sync.ISyncService;

/**
 *  This example shows multiple microservices implemented in one Java pojo
 */
@Microservice
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
