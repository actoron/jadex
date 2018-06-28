package jadex.micro.testcases.blocking;

import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  Test service executing some blocking steps.
 */
public interface IStepService
{
	/**
	 *  Perform some steps and block some milliseconds in between.
	 */
	public IIntermediateFuture<Integer>	performSteps(int steps, long millis);
	
	/**
	 *  Perform periodical steps and block some milliseconds in between.
	 */
	public ISubscriptionIntermediateFuture<Integer>	subscribeToSteps(long millis);

}
