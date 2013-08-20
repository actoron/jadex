package jadex.bridge.nonfunctional;

import jadex.bridge.service.IService;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 
 */
public class ServiceSearchIntermediateResultListener extends TerminableIntermediateDelegationResultListener<IService>
{
	/** The saved results. */
	protected List<IService> results;
	
	/** The constraints. */
	protected IServiceSearchConstraints constraints;
	
	/** The listener state. */
	protected boolean finished;
	
	/**
	 *  Create a new listener.
	 */
	public ServiceSearchIntermediateResultListener(TerminableIntermediateDelegationFuture<IService> future, 
		ITerminableIntermediateFuture<IService> src, IServiceSearchConstraints constraints)
	{
		super(future, src);
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
		
		if(constraints.getFilter()==null || constraints.getFilter().filter(result))
		{
			// If no soft constraints just delegate result
			if(constraints.getComparator()==null)
			{
				super.customIntermediateResultAvailable(result);
			}
			else
			{
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
		}
	}
	
	/**
	 *  Check if we have soft constraints and can start
	 *  notifying results.
	 */
	protected void checkNotifyResults()
	{
		// Check if we have soft constraints
		if(constraints.getComparator()!=null)
		{
			if(!isFinished())
			{
				if(constraints.isCompareStart() || constraints.isFinished())
				{
					finished = true;
					
					if(results!=null)
					{
						Collections.sort(results, constraints.getComparator());
						
						for(IService res: results)
						{
							super.customIntermediateResultAvailable(res);
						}
					}
					
					super.finished();
				}
			}
		}
	}
}
