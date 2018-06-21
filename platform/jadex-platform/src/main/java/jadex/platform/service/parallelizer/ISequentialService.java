package jadex.platform.service.parallelizer;

import jadex.commons.future.IFuture;

/**
 *  Normal domain service.
 */
public interface ISequentialService
{
	/**
	 *  Execute a simple service.
	 */
	public IFuture<String> doSequential(String data);
}
