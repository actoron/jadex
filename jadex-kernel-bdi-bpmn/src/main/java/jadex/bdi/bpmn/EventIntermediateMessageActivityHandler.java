package jadex.bdi.bpmn;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;
import jadex.bpmn.runtime.handler.DefaultActivityHandler;

/**
 *  Handler for message events.
 */
public class EventIntermediateMessageActivityHandler	extends DefaultActivityHandler
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
		// Just set thread to waiting.
		thread.setWaiting(true);
	}
}
