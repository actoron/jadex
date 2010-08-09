package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.service.SServiceProvider;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimedObject;
import jadex.service.clock.ITimer;

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
	public void	doWait(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread, final long duration)
	{
		final Future	wifuture	= new Future(); 
		SServiceProvider.getService(instance.getServiceProvider(), IClockService.class)
			.addResultListener(instance.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				ITimedObject	to	= new ITimedObject()
				{
					public void timeEventOccurred(long currenttime)
					{
						instance.notify(activity, thread, TIMER_EVENT);
					}
				};
				
				
				Object waitinfo; 
				if(duration==TICK_TIMER)
				{
					waitinfo = ((IClockService)result).createTickTimer(to);
				}
				else
				{
					waitinfo = ((IClockService)result).createTimer(duration, to);
				}
				wifuture.setResult(waitinfo);
			}
			public void exceptionOccurred(Object source, Exception exception)
			{
				wifuture.setException(exception);
			}
		}));
		
		thread.setWaitInfo(wifuture);	// Immediate result required for multiple events handler
	}
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread The process thread.
	 *  @param info The info object.
	 */
	public void cancel(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
		((IFuture)thread.getWaitInfo()).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				if(result instanceof ITimer)
				{
					((ITimer)result).cancel();
				}
				else
				{
					throw new RuntimeException("Internal timer error: "+result);
				}
			}
		});
	}
}
