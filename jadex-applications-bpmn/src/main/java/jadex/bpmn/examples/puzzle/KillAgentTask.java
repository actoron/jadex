package jadex.bpmn.examples.puzzle;

import jadex.bdi.bpmn.BpmnPlanBodyInstance;
import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Task to kill the agent.
 */
public class KillAgentTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public Object doExecute(ITaskContext context, IProcessInstance instance)
	{
		((BpmnPlanBodyInstance)instance).killAgent();
		return null;
	}
}
