package jadex.micro.testcases.multiinvoke;

import jadex.commons.future.IIntermediateFuture;

/**
 * 
 */
//@MultiService(IExampleService.class)
public interface IMExampleService
{
//	/**
//	 * 
//	 */
//	public IIntermediateFuture<IFuture<String>> getItem();
//	
//	/**
//	 * 
//	 */
//	public IIntermediateFuture<IIntermediateFuture<String>> getItems();
	
	// flattend versions
	
	/**
	 * 
	 */
	public IIntermediateFuture<String> getItem();
	
	/**
	 * 
	 */
	public IIntermediateFuture<String> getItems();
}
