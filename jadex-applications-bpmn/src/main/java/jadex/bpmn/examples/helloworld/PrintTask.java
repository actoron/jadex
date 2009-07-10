package jadex.bpmn.examples.helloworld;

import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

public class PrintTask extends AbstractTask
{
	public Object doExecute(ITaskContext context)
	{
		String text = (String)context.getParameterValue("text");
		System.out.println(text);
		return null;
	}
}
