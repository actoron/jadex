package jadex.micro.testcases.multiinvoke;

import jadex.bridge.service.annotation.TargetMethod;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

import java.util.Collection;
import java.util.List;

/**
 *  Multiplex Interface for IExampleService.
 */
public interface IMultiplexExampleService
{
	// indirect intermediate future version
	
	/**
	 *  Get an item.
	 *  @return The items of all example services.
	 */
	@TargetMethod("getItem")
	public IIntermediateFuture<IFuture<String>> getItem1();
	
	/**
	 *  Get the items.
	 *  @return The items of all example services.
	 */
	@TargetMethod("getItems")
	public IIntermediateFuture<IIntermediateFuture<String>> getItems1(int num);
	
	// indirect future version
	
	/**
	 *  Get an item.
	 *  @return The items of all example services.
	 */
	@TargetMethod("getItem")
	public IFuture<Collection<IFuture<String>>> getItem2();
	
	/**
	 *  Get the items.
	 *  @return The items of all example services.
	 */
	@TargetMethod("getItems")
	public IFuture<Collection<IIntermediateFuture<String>>> getItems2(int num);
	
	// flattened intermediate future version
		
	/**
	 *  Get an item.
	 *  @return The items of all example services.
	 */
	@TargetMethod("getItem")
	public IIntermediateFuture<String> getItem3();
	
	/**
	 *  Get the items.
	 *  @return The items of all example services.
	 */
	@TargetMethod("getItems")
	public IIntermediateFuture<String> getItems3(int num);
	
	// flattened future version
	
	/**
	 *  Get an item.
	 *  @return The items of all example services.
	 */
	@TargetMethod("getItem")
	public IFuture<Collection<String>> getItem4();
	
	/**
	 *  Get the items.
	 *  @return The items of all example services.
	 */
	@TargetMethod("getItems")
	public IFuture<Collection<String>> getItems4(int num);
	
	
	// automated task distribution
	/**
	 * 
	 */
	@CallDistributor(ExampleDistributor.class)
	public IFuture<List<Integer>> add(List<Integer> a, List<Integer> b);
}
