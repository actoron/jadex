package jadex.bridge.nonfunctional;

import jadex.bridge.service.IService;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;

import java.util.Collection;
import java.util.List;

/**
 * 
 */
public class ServiceSearchIntermediateResultListener extends TerminableIntermediateDelegationResultListener<IService>
{
	/** The saved results. */
	protected List<IService> results;
	
	/** The listener state. */
	protected boolean finished;
	
	/** The ranker. */
	protected IServiceRanker ranker;
	
	/** The termination decider. */
	protected IRankingSearchTerminationDecider decider;
	
	/**
	 *  Create a new listener.
	 */
	public ServiceSearchIntermediateResultListener(TerminableIntermediateDelegationFuture<IService> future, 
		ITerminableIntermediateFuture<IService> src, IServiceRanker ranker, IRankingSearchTerminationDecider decider)
	{
		super(future, src);
		this.ranker = ranker;
		this.decider = decider;
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
     *  Declare that the future is finished.
     */
    public void finished()
    {
    	checkNotifyResults();
    	finished = true;
    	
    	super.finished();
    }
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void customResultAvailable(Collection<IService> result)
	{
		for(IService res: result)
		{
			intermediateResultAvailable(res);
		}
		finished();
	}

	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception)
	{
		finished = true;
		super.exceptionOccurred(exception);
	}
	
	/**
	 *  Called when an intermediate result is available.
	 * @param result The result.
	 */
	public void customIntermediateResultAvailable(IService result)
	{
		// Ignore out-filtered results (those do not satisfy hard constraints)
		
		// todo: omit filter as it is propagated to the selector?!
		// genau
		
		
		if(!isFinished())
		{
			if(results!=null)
				results.add(result);
			results.add(result);
		}
		else 
		{
			// ignore result
			System.out.println("Ignoring late result: "+result);
		}
		
		checkNotifyResults();
	}
	
	/**
	 *  Check if we have soft constraints and can start
	 *  notifying results.
	 */
	protected void checkNotifyResults()
	{
		if(!isFinished())
		{
			IServiceEvaluator evaluator = ranker instanceof IServiceEvaluator? (IServiceEvaluator) ranker : null;
			decider.isStartRanking(results, evaluator).addResultListener(new IResultListener<Boolean>()
			{
				
				public void resultAvailable(Boolean result)
				{
					if (!finished)
					{
						finished = true;
						List<IService> unrankedresults = results;
						ranker.rank(unrankedresults).addResultListener(new IResultListener<List<IService>>()
						{
							public void resultAvailable(List<IService> result)
							{
								future.setResult(result);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								future.setException(exception);
							}
						});
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					future.setException(exception);
				}
			});
			
		}
	}
}
