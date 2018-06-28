package jadex.micro.testcases.serviceimpl;

import jadex.commons.future.IFuture;

/**
 *  Test service interface.
 */
public interface IInfoService
{
	/**
	 *  Get some info.
	 *  @return Some info.
	 */
	public IFuture<String> getInfo();
}
