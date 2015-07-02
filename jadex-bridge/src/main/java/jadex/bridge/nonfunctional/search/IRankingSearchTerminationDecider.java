package jadex.bridge.nonfunctional.search;

import jadex.commons.future.IFuture;

import java.util.Collection;

/**
 * Decides when to start ranking results.
 */
public interface IRankingSearchTerminationDecider<S>
{
	/**
	 *  Decides if the search should terminate and the ranking should start.
	 *  
	 *  @param currentresults The search results acquired
	 *  @param ranker The ranker used to rank services.
	 *  
	 *  @return True, to terminate the search and start ranking.
	 */
	public IFuture<Boolean> isStartRanking(Collection<S> currentresults, IServiceEvaluator evaluator);
}
