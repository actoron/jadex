package jadex.bridge;

import java.util.TimerTask;
import java.util.logging.Logger;

import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IForwardCommandFuture;
import jadex.commons.future.IFuture;
import jadex.commons.future.IFutureCommandListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IUndoneResultListener;


/**
 *  Listener that allows to automatically trigger a timeout when
 *  no result (or exception) was received after some timeout interval.
 */
public class TimeoutResultListener<E> implements IResultListener<E>, IFutureCommandListener
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
	public TimeoutResultListener(final long timeout, IExternalAccess exta, final IResultListener<E> listener)
	{
		this(timeout, exta, false, null, listener);
	}
	
	/**
	 *  Create a new listener.
	 */
	public TimeoutResultListener(final long timeout, IExternalAccess exta, final boolean realtime, Object message, final IResultListener<E> listener)
	{
		if(listener==null)
			throw new IllegalArgumentException("Listener must not null.");
		if(exta==null)
			throw new IllegalArgumentException("External access must not null.");
			
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
				notify = true;
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
				notify = true;
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
		final Object mon = this;
//		final Exception ex	= new TimeoutException();
		
		exta.scheduleStep(new ImmediateComponentStep<Void>()
		{
			public IFuture<Void> execute(final IInternalAccess ia)
			{
				SServiceProvider.getService(ia, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener<IClockService>()
				{
					public void resultAvailable(final IClockService clock)
					{
						clock.isValid().addResultListener(ia.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener<Boolean>()
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
											{
												exta.scheduleStep(new IComponentStep<Void>()
												{
													public IFuture<Void> execute(IInternalAccess ia)
													{
														if(undone && listener instanceof IUndoneResultListener)
														{
															((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(new TimeoutException("Timeout was: "+timeout+" "+message));
														}
														else
														{
															listener.exceptionOccurred(new TimeoutException("Timeout was: "+timeout+" "+message));
														}
														return IFuture.DONE;
													}
												});
											}
										}
									};
									
			//						synchronized(TimeoutResultListener.this)
									synchronized(mon)
									{
										// Do not create new timer if already notified
										if(timeout>0 && !notified)
										{
											cancel();
											if(realtime)
											{
												// each timer creates a thread!
												
//												System.out.println("create timer");
//												Timer t = new Timer();
//												TimerTask tt = new TimerTask()
//												{
//													public void run()
//													{
//														notify.run();
//													}
//												};
//												timer = tt;
//												t.schedule(tt, timeout);
												
												timer = clock.createRealtimeTimer(timeout, new ITimedObject()
												{
//													Object	timer1	= timer;
													public void timeEventOccurred(long currenttime)
													{
//														if(timer!=timer1)
//														{
//															System.out.println("wrong timer: "+message);
//														}
														notify.run();
													}

													public String toString()
													{
														return super.toString()+": "+message;
													}
												});
//												System.out.println("new real trl: "+message);
											}
											else
											{
												timer = clock.createTimer(timeout, new ITimedObject()
												{
//													Object	timer1	= timer;
													public void timeEventOccurred(long currenttime)
													{
//														if(timer!=timer1)
//														{
//															System.out.println("wrong timer: "+message);
//														}
														notify.run();
													}
													
													public String toString()
													{
														return super.toString()+": "+message;
													}
												});
//												System.out.println("new clock trl: "+message);
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
	
	/**
	 *  Called when a command is available.
	 */
	public void commandAvailable(Object command)
	{
		// reinit the timer
//		System.out.println("reinit of timer");
		if(IForwardCommandFuture.Type.UPDATETIMER.equals(command))
			initTimer();
		
		if(listener instanceof IFutureCommandListener)
		{
			((IFutureCommandListener)listener).commandAvailable(command);
		}
		else
		{
			Logger.getLogger("timeout-result-listener").fine("Cannot forward command: "+listener+" "+command);
//			System.out.println("Cannot forward command: "+listener+" "+command);
		}
	}
}
