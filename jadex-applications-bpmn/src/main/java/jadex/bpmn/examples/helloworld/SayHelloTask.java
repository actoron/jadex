package jadex.bpmn.examples.helloworld;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Print hello on the console and increment parameter 'x'.
 */
public class SayHelloTask	extends AbstractTask
{
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		System.out.println("Hello BPMN world!");
		int	x	= ((Number)context.getParameterValue("x")).intValue();
		x++;
		System.out.println("Setting x to: "+x);		
		context.setParameterValue("x", new Integer(x));
	}
}
