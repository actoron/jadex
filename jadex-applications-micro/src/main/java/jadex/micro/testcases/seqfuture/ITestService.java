package jadex.micro.testcases.seqfuture;

import jadex.commons.future.ISequenceFuture;

/**
 * 
 */
public interface ITestService
{
	/**
	 * 
	 */
	public ISequenceFuture<String, Integer> getSomeResults();
}
