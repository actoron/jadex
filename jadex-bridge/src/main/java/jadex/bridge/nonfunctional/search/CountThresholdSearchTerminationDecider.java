package jadex.bridge.nonfunctional.search;

import jadex.commons.future.Future;

import java.util.Collection;

public class CountThresholdSearchTerminationDecider<S> implements IRankingSearchTerminationDecider<S>
{
	protected int threshold;
	
	public CountThresholdSearchTerminationDecider(int threshold)
	{
		this.threshold = threshold;
	}
	
	public Future<Boolean> isStartRanking(Collection<S> currentresults,
			IServiceEvaluator evaluator)
	{
		return new Future<Boolean>(currentresults.size() >= threshold);
	}
}
