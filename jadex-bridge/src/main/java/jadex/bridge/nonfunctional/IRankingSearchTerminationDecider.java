package jadex.bridge.nonfunctional;

import jadex.bridge.service.IService;
import jadex.commons.future.Future;

import java.util.List;

/**
 * Decides when to start ranking results.
 */
public interface IRankingSearchTerminationDecider
{
	/**
	 *  Decides if the search should terminate and the ranking should start.
	 *  
	 *  @param currentresults The search results acquired
	 *  @param ranker
	 *  
	 *  @return True, to terminate the search and start ranking.
	 */
	public Future<Boolean> isStartRanking(List<IService> currentresults, IServiceEvaluator evaluator);
}
