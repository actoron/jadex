package jadex.bridge;

import java.util.Collection;

import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IUndoneIntermediateResultListener;

/**
 * 
 */
public class TimeoutIntermediateResultListener<E> extends TimeoutResultListener<Collection<E>> implements IIntermediateResultListener<E>, IUndoneIntermediateResultListener<E>
{
	/**
	 *  Create a new listener.
	 */
	public TimeoutIntermediateResultListener(final long timeout, IExternalAccess exta)
	{
		this(timeout, exta, false, null, null);
	}
	
	/**
	 *  Create a new listener.
	 */
	public TimeoutIntermediateResultListener(final long timeout, IExternalAccess exta, final IIntermediateResultListener<E> listener)
	{
		this(timeout, exta, false, null, listener);
	}
	
	/**
	 *  Create a new listener.
	 */
	public TimeoutIntermediateResultListener(final long timeout, IExternalAccess exta, final boolean realtime, Object message, final IIntermediateResultListener<E> listener)
	{
		super(timeout, exta, realtime, message, listener);
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
				notify = listener!=null;
//				notified = true;
//				cancel();
				// reinit timer on every new result (also cancels old one)
				initTimer();
			}
		}
		if(notify)
		{
			if(undone && listener instanceof IUndoneIntermediateResultListener)
			{
				((IUndoneIntermediateResultListener<E>)listener).intermediateResultAvailableIfUndone(result);
			}
			else
			{
				getIntermediateResultListener().intermediateResultAvailable(result);
			}
		}
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
				notify = listener!=null;
				notified = true;
				cancel();
			}
		}
		if(notify)
		{
			if(undone && listener instanceof IUndoneIntermediateResultListener)
			{
				((IUndoneIntermediateResultListener<E>)listener).finishedIfUndone();
			}
			else
			{
				getIntermediateResultListener().finished();
			}
		}
	}
    
	/**
	 *  Called when an intermediate result is available.
	 *  @param result The result.
	 */
	public void intermediateResultAvailableIfUndone(E result)
	{
		this.undone = true;
		intermediateResultAvailable(result);
	}
	
	/**
     *  Declare that the future is finished.
	 *  This method is only called for intermediate futures,
	 *  i.e. when this method is called it is guaranteed that the
	 *  intermediateResultAvailable method was called for all
	 *  intermediate results before.
     */
    public void finishedIfUndone()
    {
    	this.undone = true;
    	finished();
    }

	/**
	 *  Get the undone.
	 *  @return The undone.
	 */
	public boolean isUndone()
	{
		return undone;
	}
}
