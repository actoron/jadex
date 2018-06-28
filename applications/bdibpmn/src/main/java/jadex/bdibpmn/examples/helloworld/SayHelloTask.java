package jadex.bdibpmn.examples.helloworld;

import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bridge.IInternalAccess;

/**
 *  Print hello on the console and increment parameter 'x'.
 */
public class SayHelloTask	extends AbstractTask
{
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		System.out.println("Hello BPMN world!");
		int	x	= ((Number)context.getParameterValue("x")).intValue();
		x++;
		System.out.println("Setting x to: "+x);		
		context.setParameterValue("x", Integer.valueOf(x));
	}
}
