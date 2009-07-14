package jadex.bpmn.examples.helloworld;

import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Print out some text stored in variable test.
 */
public class PrintTask extends AbstractTask
{
	/**
	 * 
	 */
	public Object doExecute(ITaskContext context)
	{
		String text = (String)context.getParameterValue("text");
		System.out.println(text);
		return null;
	}
}
