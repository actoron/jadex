package jadex.bpmnbdi.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bpmnbdi.BpmnPlanBodyInstance;

/**
 *  Task to kill the agent.
 */
public class KillAgentTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		((BpmnPlanBodyInstance)instance).killAgent();
	}
}
