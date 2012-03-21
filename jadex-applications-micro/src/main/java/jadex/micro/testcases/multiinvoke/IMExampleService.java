package jadex.micro.testcases.multiinvoke;

import java.util.Collection;

import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Multiplex Interface for IExampleService.
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
	
//	/**
//	 * 
//	 */
//	public IFuture<Collection<String>> getItem();
	
	/**
	 *  Get an item.
	 *  @return The items of all example services.
	 */
	public IIntermediateFuture<String> getItem();
	
	/**
	 *  Get the items.
	 *  @return The items of all example services.
	 */
	public IIntermediateFuture<String> getItems();
}
