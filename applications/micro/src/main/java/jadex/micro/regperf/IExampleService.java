package jadex.micro.regperf;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 * 
 */
@Service
public interface IExampleService
{
	/**
	 *  Say hello method.
	 *  @param name The name.
	 */
	public IFuture<String> sayHello(String name);
}
