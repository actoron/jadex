package jadex.bpmn.examples.helloworld;

import jadex.bpmn.runtime.AbstractTask;
import jadex.bpmn.runtime.ITaskContext;

/**
 *  Print hello on the console and increment parameter 'x'.
 */
public class SayHelloTask	extends AbstractTask
{
	public Object doExecute(ITaskContext context)
	{
		System.out.println("Hello BPMN world!");
		int	x	= ((Number)context.getParameterValue("x")).intValue();
		x++;
		System.out.println("Setting x to: "+x);		
		context.setParameterValue("x", new Integer(x));
		return null;
	}
}
