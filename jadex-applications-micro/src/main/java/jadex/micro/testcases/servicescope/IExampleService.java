package jadex.micro.testcases.servicescope;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  An example service interface.
 */
@Service
public interface IExampleService
{
	/**
	 *  An example method.
	 */
	public IFuture<String> getInfo();
}
