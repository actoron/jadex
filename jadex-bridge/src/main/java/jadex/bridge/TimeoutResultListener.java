package jadex.bridge;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IResultListener;


/**
 *  Listener that allows to automatically trigger a timeout when
 *  no result (or exception) was received after some timeout interval.
 */
public class TimeoutResultListener<E> implements IResultListener<E>
{
	//-------- attributes --------
	
	/** The delegation listener. */
	protected IResultListener<E> listener;
	
	/** The external access. */
	protected IExternalAccess exta;
	
	/** The timeout occurred flag. */
	protected boolean notified;
	
	//-------- constructors --------

	/**
	 *  Create a new listener.
	 */
	public TimeoutResultListener(final long timeout, IExternalAccess exta, final IResultListener<E> listener)
	{
		if(listener==null)
			throw new IllegalArgumentException("Listener must not null.");
		if(exta==null)
			throw new IllegalArgumentException("External access must not null.");
			
		this.listener = listener;
		this.exta = exta;
		
		// Initialize timeout
		if(timeout>0)
		{
			SServiceProvider.getServiceUpwards(exta.getServiceProvider(), IClockService.class)
				.addResultListener(new DefaultResultListener<IClockService>()
			{
				public void resultAvailable(IClockService clock)
				{
					try
					{
						clock.createTimer(timeout, new ITimedObject()
						{
							public void timeEventOccurred(long currenttime)
							{
								boolean notify = false;
								synchronized(TimeoutResultListener.this)
								{
									if(!notified)
									{
										notify = true;
										notified = true;
									}
								}
								if(notify)
									listener.exceptionOccurred(new TimeoutException());
							}
						});
					}
					catch(Exception e)
					{
						// todo: should not happen
						// causes null pointer exception on clock
					}
				}
				
				public void exceptionOccurred(Exception exception)
				{
					System.out.println("Could not get clock service.");
				}
			});
		}
	}

	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailable(E result)
	{
		boolean notify = false;
		synchronized(this)
		{
			if(!notified)
			{
				notify = true;
				notified = true;
			}
		}
		if(notify)
			listener.resultAvailable(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurred(Exception exception)
	{
		boolean notify = false;
		synchronized(this)
		{
			if(!notified)
			{
				notify = true;
				notified = true;
			}
		}
		if(notify)
			listener.exceptionOccurred(exception);
	}
}
