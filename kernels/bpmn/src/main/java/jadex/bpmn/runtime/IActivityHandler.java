package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.bridge.IInternalAccess;

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
	 */
	public void execute(MActivity activity, IInternalAccess instance, ProcessThread thread);

	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread The process thread.
	 *  @param info The info object.
	 */
	public void cancel(MActivity activity, IInternalAccess instance, ProcessThread thread);

}
