package jadex.bpmn.examples.helloworld;

import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Print goodbye on the console and print parameter 'y'.
 */
public class SayGoodbyeTask	extends AbstractTask
{
	public Object doExecute(ITaskContext context)
	{
		System.out.println("Goodbye BPMN world!");
		int	y = ((Number)context.getParameterValue("y")).intValue();
		System.out.println("Parameter y: "+y);
		return null;
	}
}
