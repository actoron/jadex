package jadex.platform.service.parallelizer;

import jadex.commons.future.IIntermediateFuture;

/**
 * 
 */
public interface IParallelService
{
	/**
	 * 
	 */
	public IIntermediateFuture<String> doParallel(String[] data);
}
