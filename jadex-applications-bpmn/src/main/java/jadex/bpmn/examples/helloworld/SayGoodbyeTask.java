package jadex.bpmn.examples.helloworld;

import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;

/**
 *  Print goodbye on the console and print parameter 'y'.
 */
public class SayGoodbyeTask implements ITask
{
	public void execute(ITaskContext context, IResultListener listener)
	{
		System.out.println("Goodbye BPMN world!");
		int	y	= ((Number)context.getParameterValue("y")).intValue();
		System.out.println("Parameter y: "+y);		
	}
}
