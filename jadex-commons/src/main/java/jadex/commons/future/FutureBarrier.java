package jadex.commons.future;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.commons.ICommand;

/**
 *  A future barrier allows for waiting for a variable number
 *  of futures. Calling waitFor will wait until all futures are done.
 */
public class FutureBarrier<E> 
{
	/** The list of futures. */
	protected List<IFuture<E>> futures;

	/**
	 *  Add a future to the barrier.
	 *  @param fut The future.
	 */
	public void addFuture(IFuture<E> fut)
	{
		if(fut!=null)
		{
			if(futures==null)
				futures = new ArrayList<IFuture<E>>();
			futures.add(fut);
		}
	}
	
	/**
	 *  Wait for all added futures being finished.
	 */
	public IFuture<Void> waitFor()
	{
		Future<Void> ret = new Future<Void>();
		
		if(futures!=null)
		{
			CounterResultListener<E> lis = new CounterResultListener<E>(futures.size(), new DelegationResultListener<Void>(ret));
			for(IFuture<E> fut: futures)
			{
				fut.addResultListener(lis);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Wait for all added futures being finished.
	 *  Ignore failures and call optional failure handler, if any.
	 */
	public IFuture<Void> waitForIgnoreFailures(final ICommand<Exception> failurehandler)
	{
		Future<Void> ret = new Future<Void>();
		
		if(futures!=null)
		{
			CounterResultListener<E> lis = new CounterResultListener<E>(futures.size(), true, new DelegationResultListener<Void>(ret))
			{
				@Override
				public void exceptionOccurred(Exception exception)
				{
					if(failurehandler!=null)
					{
						failurehandler.execute(exception);
					}
					super.exceptionOccurred(exception);
				}
			};
			
			for(IFuture<E> fut: futures)
			{
				fut.addResultListener(lis);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Wait for all added futures being finished
	 *  and collect the results.
	 */
	public IFuture<Collection<E>> waitForResults()
	{
		Future<Collection<E>> ret = new Future<Collection<E>>();
		
		if(futures!=null)
		{
			CollectionResultListener<E> lis = new CollectionResultListener<E>(futures.size(), new DelegationResultListener<Collection<E>>(ret));
			for(IFuture<E> fut: futures)
			{
				fut.addResultListener(lis);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Wait for all added futures being finished and collect the results.
	 *  Ignore failures and call optional failure handler, if any.
	 */
	public IFuture<Collection<E>> waitForResultsIgnoreFailures(final ICommand<Exception> failurehandler)
	{
		Future<Collection<E>> ret = new Future<Collection<E>>();
		
		if(futures!=null)
		{
			CollectionResultListener<E> lis = new CollectionResultListener<E>(futures.size(), true, new DelegationResultListener<Collection<E>>(ret))
			{
				@Override
				public void exceptionOccurred(Exception exception)
				{
					if(failurehandler!=null)
					{
						failurehandler.execute(exception);
					}
					super.exceptionOccurred(exception);
				}
			};

			for(IFuture<E> fut: futures)
			{
				fut.addResultListener(lis);
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
}
