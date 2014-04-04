package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;

/**
 *  On error end propagate an exception.
 */
public class EventEndTerminateActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute the activity.
	 */
	protected void doExecute(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
		instance.killComponent();
	}
}
