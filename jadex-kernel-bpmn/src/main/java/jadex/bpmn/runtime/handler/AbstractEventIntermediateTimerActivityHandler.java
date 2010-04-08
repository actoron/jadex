package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;

/**
 *  Abstract handler for timing events.
 *  Can be subclassed by platform-specific implementations.
 */
public abstract class AbstractEventIntermediateTimerActivityHandler	extends DefaultActivityHandler
{
	public static final int TICK_TIMER = -2;
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
//		Number dur = (Number)getPropertyValue(activity, instance, thread, "duration");
		Number dur = (Number)thread.getPropertyValue("duration", activity);
		Boolean tmp = (Boolean)thread.getPropertyValue("tick", activity);
		boolean tick = tmp!=null? tmp.booleanValue(): false;
		long duration = dur==null? tick? TICK_TIMER: -1: dur.longValue(); 
//		thread.setWaitingState(ProcessThread.WAITING_FOR_TIME);
		thread.setWaiting(true);
		Object handle = doWait(activity, instance, thread, duration);
		thread.setWaitInfo(handle);
	}
	
	/**
	 *  Template method to be implemented by platform-specific subclasses.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param duration	The duration to wait.
	 */
	public abstract Object doWait(MActivity activity, BpmnInterpreter instance, ProcessThread thread, long duration);
}
