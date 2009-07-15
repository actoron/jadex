package jadex.bpmn.examples.puzzle;

import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;

/**
 *  Dispatch a subgoal and wait for the result.
 */
public class SubgoalTask	implements ITask
{
	/**
	 *  Execute the task.
	 */
	public void execute(ITaskContext context, IProcessInstance instance, IResultListener listener)
	{
		// todo
		listener.resultAvailable(null);
	}
}
