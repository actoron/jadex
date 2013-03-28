package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.commons.IFilter;

/**
 *  Abstract handler for timing events.
 *  Can be subclassed by platform-specific implementations.
 */
public abstract class AbstractEventIntermediateTimerActivityHandler	extends DefaultActivityHandler
{
	public static final int TICK_TIMER = -2;

	public static final String	TIMER_EVENT	= "timer-event";
	
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
//		Number dur = (Number)getPropertyValue(activity, instance, thread, "duration");
		Object d = thread.getPropertyValue("duration", activity); // does not wait if initial cron time pattern was specified "* * * * *"
		Number dur = d instanceof Number? (Number)d: null;
		Boolean tmp = (Boolean)thread.getPropertyValue("tick", activity);
		boolean tick = tmp!=null? tmp.booleanValue(): false;
		long duration = dur==null? tick? TICK_TIMER: -1: dur.longValue(); 
//		thread.setWaitingState(ProcessThread.WAITING_FOR_TIME);
		thread.setWaiting(true);
		thread.setWaitFilter(new IFilter<Object>()
		{
			public boolean filter(Object event)
			{
				return TIMER_EVENT.equals(event); 
			}
		});
		doWait(activity, instance, thread, duration);
	}
	
	/**
	 *  Template method to be implemented by platform-specific subclasses.
	 *  @param activity	The timing event activity.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param duration	The duration to wait.
	 */
	public abstract void doWait(MActivity activity, BpmnInterpreter instance, ProcessThread thread, long duration);
}
