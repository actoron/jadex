package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.ComponentTerminatedException;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimedObject;

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
	public Object doWait(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread, long duration)
	{
		IClockService cs = (IClockService)instance.getComponentAdapter().getServiceContainer().getService(IClockService.class);
		Object ret = cs.createTimer(duration, new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
				try
				{
					instance.invokeLater(new Runnable()
					{
						public void run()
						{
							EventIntermediateTimerActivityHandler.this.notify(activity, instance, thread, null);
						}
					});
				}
				catch(ComponentTerminatedException e)
				{
					// Ignore: Exception occurs in case of fatal error in component execution.
				}
			}
		});
		return ret;
	}
	
	/**
	 *  Template method to be implemented by platform-specific subclasses.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param duration	The duration to wait.
	 */
//	public void doWait(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread, long duration)
//	{
//		final Timer	timer	= new Timer();
//		timer.schedule(new TimerTask()
//		{	
//			public void run()
//			{
//				timer.cancel();
//				EventIntermediateTimerActivityHandler.this.notify(activity, instance, thread, null);
//			}
//		}, duration);
//	}
}
