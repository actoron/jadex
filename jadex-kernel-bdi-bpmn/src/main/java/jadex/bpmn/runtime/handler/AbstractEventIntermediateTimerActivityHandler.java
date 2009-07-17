package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.ProcessThread;

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
	 */
	public void execute(MActivity activity, BpmnInstance instance, ProcessThread thread)
	{
//		Number dur = (Number)getPropertyValue(activity, instance, thread, "duration");
		Number dur = (Number)thread.getPropertyValue("duration");
		long duration = dur==null? -1: dur.longValue(); 
//		thread.setWaitingState(ProcessThread.WAITING_FOR_TIME);
		thread.setWaiting(true);
		doWait(activity, instance, thread, duration);
	}
	
	/**
	 *  Template method to be implemented by platform-specific subclasses.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param duration	The duration to wait.
	 */
	public abstract void doWait(MActivity activity, BpmnInstance instance, ProcessThread thread, long duration);
}
