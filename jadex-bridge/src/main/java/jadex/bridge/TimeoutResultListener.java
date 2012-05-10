package jadex.bridge;

import java.util.Timer;
import java.util.TimerTask;

import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
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
	
	/** The timer. */
	protected Object timer;
	
	/** The timeout. */
	protected long timeout;
	
	/** The realtime flag. */
	protected boolean realtime;
	
	//-------- constructors --------

	/**
	 *  Create a new listener.
	 */
	public TimeoutResultListener(final long timeout, IExternalAccess exta, final IResultListener<E> listener)
	{
		this(timeout, exta, false, listener);
	}
	
	/**
	 *  Create a new listener.
	 */
	public TimeoutResultListener(final long timeout, IExternalAccess exta, final boolean realtime,
		final IResultListener<E> listener)
	{
		if(listener==null)
			throw new IllegalArgumentException("Listener must not null.");
		if(exta==null)
			throw new IllegalArgumentException("External access must not null.");
			
		this.listener = listener;
		this.exta = exta;
		this.timeout = timeout;
		this.realtime = realtime;
		
		initTimer();
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
				cancel();
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
				cancel();
			}
		}
		if(notify)
			listener.exceptionOccurred(exception);
	}
	
	/**
	 *  Cancel the timeout.
	 */
	public synchronized void cancel()
	{
		if(timer instanceof TimerTask)
		{
			((TimerTask)timer).cancel();
		}
		else if(timer instanceof ITimer)
		{
			((ITimer)timer).cancel();
		}
	}
	
	/**
	 * 
	 */
	protected synchronized void initTimer()
	{
		// Initialize timeout
		final Object mon = this;
		
		exta.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				SServiceProvider.getServiceUpwards(exta.getServiceProvider(), IClockService.class)
					.addResultListener(ia.createResultListener(new DefaultResultListener<IClockService>()
				{
					public void resultAvailable(final IClockService clock)
					{
						clock.isValid().addResultListener(ia.createResultListener(new DefaultResultListener<Boolean>()
						{
							public void resultAvailable(Boolean valid)
							{
								if(!valid.booleanValue())
								{
//									System.out.println("invalid clock");
									return;
								}
								
								try
								{
									final Runnable notify = new Runnable()
									{
										public void run()
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
									};
									
			//						synchronized(TimeoutResultListener.this)
									synchronized(mon)
									{
										// Do not create new timer if already notified
										if(timeout>0 && !notified)
										{
											if(realtime && !IClock.TYPE_SYSTEM.equals(clock.getClockType()))
											{
												Timer t = new Timer();
												TimerTask tt = new TimerTask()
												{
													public void run()
													{
														notify.run();
													}
												};
												timer = tt;
												t.schedule(tt, timeout);
											}
											else
											{
												timer = clock.createTimer(timeout, new ITimedObject()
												{
													public void timeEventOccurred(long currenttime)
													{
														notify.run();
													}
												});
											}
										}
									}
								}
								catch(Exception e)
								{
									e.printStackTrace();
									// todo: should not happen
									// causes null pointer exception on clock when clock is uninitialized
								}
							}
						}));
					}
					public void exceptionOccurred(Exception exception)
					{
	//					exception.printStackTrace();
	//					System.out.println("Could not get clock service.");
					}
				}));
				
				return IFuture.DONE;
			}
		});
	}
}
