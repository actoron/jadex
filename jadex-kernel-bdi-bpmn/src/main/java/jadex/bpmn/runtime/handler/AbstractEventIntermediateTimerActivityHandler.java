package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;

/**
 *  Abstract handler for timing events.
 *  Can be subclassed by platform-specific implementations.
 */
public abstract class AbstractEventIntermediateTimerActivityHandler	extends DefaultActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param context	The thread context.
	 */
	public void execute(MActivity activity, BpmnInstance instance, ProcessThread thread, ThreadContext context)
	{
		Number dur = (Number)activity.getPropertyValue("duration");
		long duration = dur==null? -1: dur.longValue(); 
		thread.setWaiting(true);
		doWait(activity, instance, thread, context, duration);
	}
	
	/**
	 *  Template method to be implemented by platform-specific subclasses.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param context	The thread context.
	 *  @param duration	The duration to wait.
	 */
	public abstract void doWait(MActivity activity, BpmnInstance instance, ProcessThread thread, ThreadContext context, long duration);
}
