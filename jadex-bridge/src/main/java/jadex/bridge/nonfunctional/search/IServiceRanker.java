package jadex.bridge.nonfunctional.search;

import jadex.commons.future.IFuture;

import java.util.List;

/**
 *  Interfaces for non-functional ranking mechanism for services.
 *
 */
public interface IServiceRanker<S>
{
	/**
	 *  Ranks services according to non-functional criteria.
	 *  
	 *  @param unrankedservices Unranked list of services.
	 *  @return Ranked list of services.
	 */
	public IFuture<List<S>> rank(List<S> unrankedservices);
}
