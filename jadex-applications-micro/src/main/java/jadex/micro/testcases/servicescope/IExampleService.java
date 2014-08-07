package jadex.micro.testcases.servicescope;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 * 
 */
@Service
public interface IExampleService
{
	/**
	 * 
	 */
	public IFuture<String> getInfo();
}
