package jadex.bdiv3.testcases.servicereflection;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  An example service interface that is used from micro tests and not naturally visible.
 */
@Service
public interface INotVisibleService
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
