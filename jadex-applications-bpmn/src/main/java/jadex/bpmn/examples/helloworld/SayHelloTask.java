package jadex.bpmn.examples.helloworld;

import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;

/**
 *  Print hello on the console and increment parameter 'x'.
 */
public class SayHelloTask implements ITask
{
	public void execute(ITaskContext context, IResultListener listener)
	{
		System.out.println("Hello BPMN world!");
		int	x	= ((Number)context.getParameterValue("x")).intValue();
		x++;
		System.out.println("Setting x to: "+x);		
		context.setParameterValue("x", new Integer(x));
	}
}
