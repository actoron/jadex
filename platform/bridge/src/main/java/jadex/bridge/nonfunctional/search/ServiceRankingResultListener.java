package jadex.bridge.nonfunctional.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.commons.Tuple2;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;

/**
 *  Listener that ranks results.
 */
public class ServiceRankingResultListener<S> implements IIntermediateResultListener<S>
{
	/** The saved results. */
	protected List<S> results = new ArrayList<S>();
	
	/** The listener state. */
	protected boolean finished;
	
	/** The ranker. */
	protected IServiceRanker<S> ranker;
	
	/** The termination decider. */
	protected IRankingSearchTerminationDecider<S> decider;
	
	/** The delegate listener. */
	protected IResultListener<Collection<S>> listener;
	
	/** The delegate listener. */
	protected IResultListener<Collection<Tuple2<S, Double>>> scorelistener;

	
	/**
	 *  Create a new ranker.
	 */
	public ServiceRankingResultListener(IServiceRanker<S> ranker, IRankingSearchTerminationDecider<S> decider, IResultListener<Collection<S>> listener)
	{
		this.ranker = ranker;
		this.decider = decider;
		this.listener = listener;
	}
	
	/**
	 *  Create a new ranker.
	 */
	public ServiceRankingResultListener(IResultListener<Collection<Tuple2<S, Double>>> scorelistener, IServiceRanker<S> ranker, IRankingSearchTerminationDecider<S> decider)
	{
		this.ranker = ranker;
		this.decider = decider;
		this.scorelistener = scorelistener;
	}
	
	/**
	 *  Process intermediate results for ranking.
	 */
	public void intermediateResultAvailable(S result)
	{
		if(!finished)
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
	public void resultAvailable(Collection<S> result)
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
		return finished;
	}
	
	/**
	 *  Rank the results and announce them
	 */
	protected void rankResults()
	{
		if(!isFinished())
		{
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
			finished = true;
			
			if(listener instanceof IIntermediateResultListener)
			{
				IIntermediateResultListener<S> lis = (IIntermediateResultListener<S>)listener;
				for(Tuple2<S, Double> res: results)
				{
					lis.intermediateResultAvailable(res.getFirstEntity());
				}
				lis.finished();
			}
			else if(listener!=null)
			{
				List<S> res = new ArrayList<S>();
				for(Tuple2<S, Double> ser: results)
				{
					res.add(ser.getFirstEntity());
				}
				listener.resultAvailable(res);
			}
			else if(scorelistener instanceof IIntermediateResultListener)
			{
				IIntermediateResultListener<Tuple2<S, Double>> lis = (IIntermediateResultListener<Tuple2<S, Double>>)scorelistener;
				for(int i=0; i<results.size(); i++)
				{
					lis.intermediateResultAvailable(results.get(i));
				}
				lis.finished();
			}
			else if(scorelistener!=null)
			{
				scorelistener.resultAvailable(results);
			}
		}
	}
	
	/**
	 * 
	 */
	protected void notifyException(Exception exception)
	{
		if(!isFinished())
		{
			finished = true;
			if(listener!=null)
			{
				listener.exceptionOccurred(exception);
			}
			else
			{
				scorelistener.exceptionOccurred(exception);
			}
		}
	}
}
