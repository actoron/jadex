package jadex.bridge;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.DebugException;
import jadex.commons.TimeoutException;
import jadex.commons.future.ExceptionResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IForwardCommandFuture;
import jadex.commons.future.IFuture;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IUndoneResultListener;


/**
 *  Listener that allows to automatically trigger a timeout when
 *  no result (or exception) was received after some timeout interval.
 */
// TODO: does not work reliably during shutdown (cf. chat service status publish 2 sec timeout)
public class TimeoutResultListener<E> implements IResultListener<E>, IUndoneResultListener<E>, IFutureCommandResultListener<E>
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
	
	/** The timeout message. */
	protected Object message;
	
	/** Flag if undone methods should be used. */
	protected boolean undone;
	
	//-------- constructors --------

	/**
	 *  Create a new listener.
	 */
	public TimeoutResultListener(final long timeout, IExternalAccess exta)
	{
		this(timeout, exta, false, null, null);
	}
	
	/**
	 *  Create a new listener.
	 */
	public TimeoutResultListener(final long timeout, IExternalAccess exta, final IResultListener<E> listener)
	{
		this(timeout, exta, false, null, listener);
	}
	
	/**
	 *  Create a new listener.
	 */
	public TimeoutResultListener(final long timeout, IExternalAccess exta, final boolean realtime, Object message, final IResultListener<E> listener)
	{
		if(exta==null)
			throw new IllegalArgumentException("External access must not null.");
			
//		// For checking that tests use sim only
//		if(realtime)
//			throw new UnsupportedOperationException();
			
		this.exta	= exta;
		this.listener	= listener;
		this.timeout	= timeout;
		this.realtime	= realtime;
		this.message	= message;
		
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
				notify = listener!=null;
				notified = true;
				cancel();
			}
		}
		if(notify)
		{
			if(undone && listener instanceof IUndoneResultListener)
			{
				((IUndoneResultListener<E>)listener).resultAvailableIfUndone(result);
			}
			else
			{
				listener.resultAvailable(result);
			}
		}
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
				// need to further delegate to chained listeners/futures?
				notify = listener!=null;
				notified = true;
				cancel();
			}
		}
		if(notify)
		{
			if(undone && listener instanceof IUndoneResultListener)
			{
				((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(exception);
			}
			else
			{
				listener.exceptionOccurred(exception);
			}
		}
	}
	
    /**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailableIfUndone(E result)
	{
		this.undone = true;
		resultAvailable(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurredIfUndone(Exception exception)
	{
		this.undone = true;
		exceptionOccurred(exception);
	}

	/**
	 *  Cancel the timeout.
	 */
	public synchronized void cancel()
	{
		if(timer==null)
			return;
		
		if(timer instanceof TimerTask)
		{
			((TimerTask)timer).cancel();
		}
		else //if(timer instanceof ITimer)
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
		final Exception ex	= Future.DEBUG ? new DebugException() : null;
		
		exta.scheduleStep(new IPriorityComponentStep<Void>()
		{
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				try
				{
					IClockService clock	= ia.getFeature(IRequiredServicesFeature.class).getLocalService(new ServiceQuery<>(IClockService.class));
					
					synchronized(TimeoutResultListener.this)
					{
						// Do not create new timer if already notified
						if(timeout>0 && !notified)
						{
							cancel();
							if(realtime)
							{
								timer = clock.createRealtimeTimer(timeout, new ITimedObject()
								{
									public void timeEventOccurred(long currenttime)
									{
										createTimerTask(ex).run();
									}
	
									public String toString()
									{
										return super.toString()+": "+message;
									}
								});
							}
							else
							{
								timer = clock.createTimer(timeout, new ITimedObject()
								{
									public void timeEventOccurred(long currenttime)
									{
										createTimerTask(ex).run();
									}
									
									public String toString()
									{
										return super.toString()+": "+message;
									}
								});
							}
						}
					}
					return IFuture.DONE;
				}
				catch(Exception e)
				{
					return new Future<Void>(e);
				}
			}
		}).addResultListener(new ExceptionResultListener<Void>()
		{
			@Override
			public void exceptionOccurred(Exception exception)
			{
				// When platform timer cannot be inited use java timer for realtime, but fail on sim time
				if(realtime)
				{
					TimerTask	notify	= createTimerTask(ex);
					new Timer(true).schedule(notify, timeout);
					timer	= notify;
				}
				else
				{
					TimeoutResultListener.this.exceptionOccurred(exception);
				}
			}
		});
	}

	protected TimerTask createTimerTask(final Exception debug)
	{
		return new TimerTask()
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
				{
					exta.scheduleStep(new IComponentStep<Void>()
					{
						public IFuture<Void> execute(IInternalAccess ia)
						{
							TimeoutException te	= new TimeoutException("Timeout was: "+timeout+(realtime?" (realtime) ":" ")+message+(Future.DEBUG ? "" : ". Use PlatformConfiguration.getExtendedPlatformConfiguration().setDebugFutures(true) for timeout cause."), debug);
							timeoutOccurred(te);
							return IFuture.DONE;
						}
					});
				}
			}
		};
	}
	
	/**
	 *  Called when a command is available.
	 */
	public void commandAvailable(Object command)
	{
		// reinit the timer
//		System.out.println("reinit of timer");
		if(IForwardCommandFuture.Type.UPDATETIMER.equals(command))
			initTimer();
		
		if(listener instanceof IFutureCommandResultListener)
		{
			((IFutureCommandResultListener<?>)listener).commandAvailable(command);
		}
		else
		{
			Logger.getLogger("timeout-result-listener").fine("Cannot forward command: "+listener+" "+command);
//			System.out.println("Cannot forward command: "+listener+" "+command);
		}
	}

	/**
	 *  Can be overridden, e.g. when no listener is used.
	 */
	public void timeoutOccurred(TimeoutException te)
	{
		if(listener!=null)
		{
			if(undone && listener instanceof IUndoneResultListener)
			{
				((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(te);
			}
			else
			{
				listener.exceptionOccurred(te);
			}
		}
	}

}
