package jadex.micro.testcases.multiinvoke;

import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 *  An example service.
 */
public interface IExampleService
{
	/**
	 *  Get an item.
	 */
	public IFuture<String> getItem();
	
	/**
	 *  Get all items.
	 */
	public IIntermediateFuture<String> getItems(int num);
	
	/**
	 *  Add two ints.
	 */
	public IFuture<Integer> add(int a, int b);
}
