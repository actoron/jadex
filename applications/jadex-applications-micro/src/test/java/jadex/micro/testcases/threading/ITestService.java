package jadex.micro.testcases.threading;

import jadex.commons.future.IFuture;


/**
 * 
 */
public interface ITestService
{
	/**
	 * 
	 */
	public IFuture<Void> testThreading();
	
}
