package jadex.bridge;

import java.util.Collection;

import jadex.commons.future.IIntermediateResultListener;

/**
 * 
 */
public class TimeoutIntermediateResultListener<E> extends TimeoutResultListener<Collection<E>> implements IIntermediateResultListener<E>
{
	/**
	 *  Create a new listener.
	 */
	public TimeoutIntermediateResultListener(final long timeout, IExternalAccess exta, final IIntermediateResultListener<E> listener)
	{
		this(timeout, exta, false, listener);
	}
	
	/**
	 *  Create a new listener.
	 */
	public TimeoutIntermediateResultListener(final long timeout, IExternalAccess exta, final boolean realtime,
		final IIntermediateResultListener<E> listener)
	{
		super(timeout, exta, realtime, listener);
	}
	
	/**
	 * 
	 */
	protected IIntermediateResultListener<E> getIntermediateResultListener()
	{
		return (IIntermediateResultListener<E>)listener;
	}
	
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailable(E result)
	{
		boolean notify = false;
		synchronized(this)
		{
			if(!notified)
			{
				notify = true;
//				notified = true;
				cancel();
				// reinit timer on every new result
				initTimer();
			}
		}
		if(notify)
			getIntermediateResultListener().intermediateResultAvailable(result);
	}
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
    public void finished()
    {
    	boolean notify = false;
		synchronized(this)
		{
			if(!notified)
			{
				notify = true;
				notified = true;
				cancel();
			}
		}
		if(notify)
			getIntermediateResultListener().finished();
    }
}
