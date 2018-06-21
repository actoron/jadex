package jadex.platform.service.parallelizer;

import jadex.commons.future.IIntermediateFuture;

/**
 *  Example service that needs to execute parallel tasks.
 */
public interface IParallelService
{
	/**
	 *  Method that wants to process data in parallel.
	 */
	public IIntermediateFuture<String> doParallel(String[] data);
}
