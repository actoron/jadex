package jadex.web.examples.hellobdiv3;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Hello service interface.
 */
@Service
public interface IHelloService
{
	/**
	 *  Say hello method.
	 */
	public IFuture<String> sayHello();
}
