package jadex.microservice.examples.async;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Example interface for service.
 */
@Service
public interface IAsyncService
{
	/**
	 *  Say hello method.
	 *  @param name The name.
	 */
	public IFuture<String> sayMeHello(String name);
}
