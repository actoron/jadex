package jadex.bpmn.examples.helloworld;

import java.util.Timer;
import java.util.TimerTask;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.AbstractEventIntermediateTimerActivityHandler;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.ProcessThread;

/**
 * 
 *
 */
public class EventIntermediateTimerActivityHandler extends	AbstractEventIntermediateTimerActivityHandler
{
	/**
	 * 
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
