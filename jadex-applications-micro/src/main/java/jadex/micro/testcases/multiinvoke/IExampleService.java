package jadex.micro.testcases.multiinvoke;

import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 * 
 */
public interface IExampleService
{
	/**
	 * 
	 */
	public IFuture<String> getItem();
	
	/**
	 * 
	 */
	public IIntermediateFuture<String> getItems();

}
