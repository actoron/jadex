package jadex.micro.testcases;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface IDService
{
	/**
	 * 
	 */
	public IFuture<Boolean> testServiceArgument(IDService service);
}
