package jadex.bridge.nonfunctional.search;

import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

import java.util.ArrayList;
import java.util.List;

public abstract class ServiceRankingResultListener<S> implements IIntermediateResultListener<S>
{
	/** The saved results. */
	protected List<S> results = new ArrayList<S>();
	
	/** The listener state. */
	protected boolean finished;
	
	/** The ranker. */
	protected IServiceRanker<S> ranker;
	
	/** The termination decider. */
	protected IRankingSearchTerminationDecider<S> decider;
	
	public ServiceRankingResultListener(IServiceRanker<S> ranker, IRankingSearchTerminationDecider<S> decider)
	{
		this.ranker = ranker;
		this.decider = decider;
	}
	
	/**
	 *  Process intermediate results for ranking.
	 */
	public void intermediateResultAvailable(S result)
	{
		if (!finished)
		{
			results.add(result);
			decider.isStartRanking(results, ranker instanceof IServiceEvaluator? (IServiceEvaluator) ranker : null).addResultListener(new IResultListener<Boolean>()
			{
				
				public void resultAvailable(Boolean result)
				{
					if (!isFinished() && result)
					{
						finished = true;
						rankResults();
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					finished = true;
					ServiceRankingResultListener.this.exceptionOccurred(exception);
				}
			});
		}
		
		customIntermediateResultAvailable(result);
	}
	
	/**
	 *  Called when an intermediate result is available.
	 * @param result The result.
	 */
	public void customIntermediateResultAvailable(S result)
	{
	}

	public void finished()
	{
		if (!isFinished())
		{
			finished = true;
			rankResults();
		}
	}
	
	/**
	 *  Get the finished.
	 *  @return The finished.
	 */
	public boolean isFinished()
	{
		return finished;
	}
	
	protected void rankResults()
	{
		ranker.rank(results).addResultListener(new IResultListener<List<S>>()
		{
			public void resultAvailable(List<S> result)
			{
				ServiceRankingResultListener.this.resultAvailable(result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ServiceRankingResultListener.this.exceptionOccurred(exception);
			}
		});
	}
}
