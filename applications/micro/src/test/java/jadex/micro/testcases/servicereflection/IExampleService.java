package jadex.micro.testcases.servicereflection;

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
	
	/**
	 *  Another example method.
	 */
	public IFuture<Integer> add(int a, int b);
}
