package jadex.web.examples.hellobdiv3;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

@Service
public interface ISayHelloService
{
	/**
	 *  Say hello method.
	 *  @return Say hello text.
	 */
	public IFuture<String> sayHello();
}
