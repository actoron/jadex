package jadex.bpmn.runtime.handler.basic;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.handler.AbstractEventIntermediateTimerActivityHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 *  Simple platform specific timer implementation.
 *  Uses java.util.Timer for testing purposes.
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
	public void doWait(final MActivity activity, final BpmnInstance instance, final ProcessThread thread, long duration)
	{
		final Timer	timer	= new Timer();
		timer.schedule(new TimerTask()
		{	
			public void run()
			{
				timer.cancel();
				EventIntermediateTimerActivityHandler.this.notify(activity, instance, thread);
			}
		}, duration);
	}
}
