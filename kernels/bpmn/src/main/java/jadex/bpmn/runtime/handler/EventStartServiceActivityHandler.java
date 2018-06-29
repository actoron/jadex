/**
 * 
 */
package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MSubProcess;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bridge.IInternalAccess;

/**
 *
 */
public class EventStartServiceActivityHandler extends EventIntermediateServiceActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 */
	public void execute(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
		// Top level event -> just move forward to next activity.
		// Or start event of event subprocess -> just move forward.
		if(thread.getParent().getParent()==null	// check that parent thread is the top thread.
			|| (thread.getParent().getModelElement() instanceof MSubProcess
			&& MSubProcess.SUBPROCESSTYPE_EVENT.equals(((MSubProcess)thread.getParent().getModelElement()).getSubprocessType())))
		{
			doExecute(activity, instance, thread);
			getBpmnFeature(instance).step(activity, instance, thread, null);
		}
		
		// Internal subprocess -> treat like intermediate event.
		else
		{
			super.execute(activity, instance, thread);
		}
	}
}
