package jadex.micro.testcases.semiautomatic.monitoring;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ITestService
{
	/**
	 * 
	 */
	public IFuture<Void> test(int level);
}
