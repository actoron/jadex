package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITaskContext;

/**
 *  Print out some text stored in variable test.
 */
public class PrintTask extends AbstractTask
{
	/**
	 * 
	 */
	public void doExecute(ITaskContext context, IProcessInstance instance)
	{
		String text = (String)context.getParameterValue("text");
		System.out.println(text);
	}
}
