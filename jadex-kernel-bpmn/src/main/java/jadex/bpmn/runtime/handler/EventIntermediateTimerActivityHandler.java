package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.clock.ITimedObject;
import jadex.bridge.service.types.clock.ITimer;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Uses timer service for implementing waiting.
 *  //Simple platform specific timer implementation.
 *  //Uses java.util.Timer for testing purposes.
 */
public class EventIntermediateTimerActivityHandler extends	AbstractEventIntermediateTimerActivityHandler
{
	/**
	 *  Template method to be implemented by platform-specific subclasses.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param duration	The duration to wait.
	 */
	public void	doWait(final MActivity activity, final IInternalAccess instance, final ProcessThread thread, final long duration)
	{
		final Future<ITimer>	wifuture	= new Future<ITimer>(); 
		SServiceProvider.getService(instance, IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(instance.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<IClockService>()
		{
			public void resultAvailable(final IClockService cs)
			{
				ITimedObject	to	= new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						try
						{
//							System.out.println("timer notification: "+activity+", "+thread+", "+this);
							getBpmnFeature(instance).notify(activity, thread, TIMER_EVENT);
						}
						catch(ComponentTerminatedException cte)
						{
							// ignore outdated timers, e.g. when process was terminated with fatal error.
						}
					}
					
					@Override
					public String toString()
					{
						return "Timer event for "+thread;
					}
				};
				
				ITimer timer; 
				if(duration==TICK_TIMER)
				{
					timer = cs.createTickTimer(to);
				}
				else
				{
					timer = cs.createTimer(duration, to);
				}
				wifuture.setResult(timer);
			}
			public void exceptionOccurred(Exception exception)
			{
				wifuture.setException(exception);
			}
		}));
		
		ICancelable ca = new ICancelable()
		{
			public IFuture<Void> cancel()
			{
				final Future<Void> ret = new Future<Void>();
				wifuture.addResultListener(new ExceptionDelegationResultListener<ITimer, Void>(ret)
				{
					public void customResultAvailable(ITimer timer)
					{
						timer.cancel();
					}
				});
				return ret;
			}
		};
		
		thread.setWaitInfo(ca);	
	}
	
//	/**
//	 *  Execute an activity.
//	 *  @param activity	The activity to execute.
//	 *  @param instance	The process instance.
//	 *  @param thread The process thread.
//	 *  @param info The info object.
//	 */
//	public void cancel(final MActivity activity, BpmnInterpreter instance, final ProcessThread thread)
//	{
//		ICancelable ca = (ICancelable)thread.getWaitInfo();
//		ca.cancel(); // todo: wait?
//	
////		System.out.println(instance.getComponentIdentifier()+" cancel called: "+activity+", "+thread);
//		((IFuture)thread.getWaitInfo()).addResultListener(new DefaultResultListener()
//		{
//			public void resultAvailable(Object result)
//			{
////				System.out.println("executing cancel: "+activity+", "+thread+", "+result);
//				if(result instanceof ITimer)
//				{
//					((ITimer)result).cancel();
//				}
//				else
//				{
//					throw new RuntimeException("Internal timer error: "+result);
//				}
//			}
//		});
//	}
}
