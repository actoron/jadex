package jadex.micro.testcases.semiautomatic;

import jadex.commons.future.IIntermediateFuture;

/**
 *  This service provides a choosable number of intermediate results.
 */
public interface IIntermediateResultsService
{
	/**
	 *  The method provides the integers 1..number as intermediate results.
	 */
	public IIntermediateFuture<Integer>	getResults(int number);
}
