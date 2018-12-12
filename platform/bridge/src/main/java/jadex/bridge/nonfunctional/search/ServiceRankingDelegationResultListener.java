package jadex.bridge.nonfunctional.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.commons.Tuple2;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;

/**
 *  Listener that ranks results.
 */
public class ServiceRankingDelegationResultListener<S> extends TerminableIntermediateDelegationResultListener<S>
{
	/** The saved results. */
	protected List<S> results = new ArrayList<S>();
	
	/** The listener state (false=unfinished, null=finishing, true=finished. */
	protected Boolean finished = Boolean.FALSE;
	
	/** The ranker. */
	protected IServiceRanker<S> ranker;
	
	/** The termination decider. */
	protected IRankingSearchTerminationDecider<S> decider;
	
	/**
	 *  Create a new ranker.
	 */
	public ServiceRankingDelegationResultListener(TerminableIntermediateDelegationFuture<S> future, ITerminableIntermediateFuture<S> src, 
		IServiceRanker<S> ranker, IRankingSearchTerminationDecider<S> decider)
	{
		super(future, src);
		this.ranker = ranker;
		this.decider = decider;
	}
	
	/**
	 *  Process intermediate results for ranking.
	 */
	public void customIntermediateResultAvailable(S result)
	{
		if(!isFinished() && !isFinishing())
		{			
			results.add(result);
			if(decider!=null)
			{
				decider.isStartRanking(results, ranker instanceof IServiceEvaluator? (IServiceEvaluator) ranker : null).addResultListener(new IResultListener<Boolean>()
				{
					public void resultAvailable(Boolean result)
					{
						if(!isFinished() && result)
						{
							if(Boolean.FALSE.equals(finished))
								finished = null; // set finishing
							
							rankResults();
						}
					}
					
					public void exceptionOccurred(Exception exception)
					{
						notifyException(exception);
					}
				});
			}
		}
		else
		{
			System.out.println("Ignoring late result: "+result);
		}
//		customIntermediateResultAvailable(result);
	}
	
	/**
	 *  Called when result is available.
	 */
	public void customResultAvailable(Collection<S> result)
	{
		for(S res: result)
		{
			intermediateResultAvailable(res);
		}
		finished();
	}
	
	/**
	 *  Called when exception occurs.
	 */
	public void exceptionOccurred(Exception exception)
	{
		notifyException(exception);
	}
	
	/**
	 * 
	 */
	public void finished()
	{
		rankResults();
	}
	
	/**
	 *  Get the finished.
	 *  @return The finished.
	 */
	public boolean isFinished()
	{
		return finished!=null? finished.booleanValue(): Boolean.FALSE;
	}
	
	/**
	 *  Get the finished.
	 *  @return The finished.
	 */
	public boolean isFinishing()
	{
		return finished==null;
	}
	
	/**
	 *  Rank the results and announce them
	 */
	protected void rankResults()
	{
		if(!isFinished())
		{
			// Terminate the source
			((TerminableIntermediateDelegationFuture<S>)future).getSource().terminate();
			
			ranker.rankWithScores(results).addResultListener(new IResultListener<List<Tuple2<S, Double>>>()
			{
				public void resultAvailable(List<Tuple2<S, Double>> result)
				{
					notifyResults(result);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					notifyException(exception);
				}
			});
		}
	}
	
	/**
	 * 
	 */
	protected void notifyResults(List<Tuple2<S, Double>> results)
	{
		if(!isFinished())
		{
			finished = Boolean.TRUE;
			
			for(Tuple2<S, Double> res: results)
			{
				future.addIntermediateResult(res.getFirstEntity());
			}
			future.setFinished();
		}
	}
	
	/**
	 * 
	 */
	protected void notifyException(Exception exception)
	{
		if(!isFinished())
		{
			finished = Boolean.TRUE;
			future.setException(exception);
		}
	}
}

