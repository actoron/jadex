package jadex.micro.testcases.blocking;

import jadex.commons.future.IFuture;

/**
 *  A test service that blocks for a given amount of time.
 */
public interface IBlockService
{
	/**
	 *  Block until the given time has passed.
	 */
	public IFuture<Void>	block(long millis);
}
