package jadex.micro.testcases.blocking;

import jadex.commons.future.IIntermediateFuture;

/**
 *  Test service executing some blocking steps.
 */
public interface IStepService
{
	/**
	 *  Perform some steps and block some milliseconds in between.
	 */
	public IIntermediateFuture<Integer>	performSteps(int steps, long millis);
}
