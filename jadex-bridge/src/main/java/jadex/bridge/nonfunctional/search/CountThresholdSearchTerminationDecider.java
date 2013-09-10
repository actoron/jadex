package jadex.bridge.nonfunctional.search;

import jadex.commons.future.Future;

import java.util.Collection;

/**
 *  Service search ranking decider based on a simple service count threshold.
 */
public class CountThresholdSearchTerminationDecider<S> implements IRankingSearchTerminationDecider<S>
{
	/** The threshold of found services after which the ranking starts. */
	protected int threshold;
	
	/**
	 *  Creates the decider.
	 *  @param threshold The threshold of found services after which the ranking starts.
	 */
	public CountThresholdSearchTerminationDecider(int threshold)
	{
		this.threshold = threshold;
	}
	
	/**
	 *  Decides if the search should start ranking.
	 */
	public Future<Boolean> isStartRanking(Collection<S> currentresults, IServiceEvaluator evaluator)
	{
		return new Future<Boolean>(currentresults.size() >= threshold);
	}
}
