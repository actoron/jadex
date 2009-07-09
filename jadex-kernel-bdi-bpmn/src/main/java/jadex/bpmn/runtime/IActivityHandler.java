package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;

/**
 *  Handler for executing a BPMN process activity.
 */
public interface IActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param context	The thread context.
	 */
	public void execute(MActivity activity, BpmnInstance instance, ProcessThread thread, ThreadContext context);
}
