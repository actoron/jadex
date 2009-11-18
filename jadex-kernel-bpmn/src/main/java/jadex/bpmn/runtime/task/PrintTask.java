package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;

/**
 *  Print out some text stored in variable test.
 */
public class PrintTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		String text = (String)context.getParameterValue("text");
		System.out.println(text);
	}
}
