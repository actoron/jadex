package jadex.bdi.testcases.service;

import jadex.commons.future.IFuture;

/**
 *  Simple interface for fetching a belief.
 */
public interface IBeliefGetter
{
	/**
	 *  Get the fact of a belief.
	 *  @param belname The belief name.
	 *  @return The fact.
	 */
	public IFuture getFact(String belname);
}
