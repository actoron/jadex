package jadex.micro.testcases.seqfuture;

import jadex.commons.future.IIntermediateFuture;
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
//	public IIntermediateFuture<String> getSomeResults();
}
