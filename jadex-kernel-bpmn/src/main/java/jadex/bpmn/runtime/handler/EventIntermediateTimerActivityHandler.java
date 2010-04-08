package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.CheckedAction;
import jadex.bridge.InterpreterTimedObject;
import jadex.service.clock.IClockService;
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
	public Object doWait(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread, long duration)
	{
		IClockService cs = (IClockService)instance.getComponentAdapter().getServiceContainer().getService(IClockService.class);
		
		CheckedAction ta = new CheckedAction()
		{
			public void run()
			{
				instance.notify(activity, thread, null);
			}
		};
		
		Object ret; 
		if(duration==TICK_TIMER)
		{
			ret = cs.createTickTimer(new InterpreterTimedObject(instance.getComponentAdapter(), ta));
		}
		else
		{
			ret = cs.createTimer(duration, new InterpreterTimedObject(instance.getComponentAdapter(), ta));
		}
		return ret;
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
		Object wi = thread.getWaitInfo();
		if(wi instanceof ITimer)
		{
			((ITimer)wi).cancel();
		}
		else
		{
			throw new RuntimeException("Internal timer error: "+wi);
		}
	}
}
