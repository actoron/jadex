package jadex.platform.service.parallelizer;

import jadex.commons.future.IFuture;

/**
 * 
 */
public interface ISequentialService
{
	/**
	 * 
	 */
	public IFuture<String> doSequential(String data);
}
