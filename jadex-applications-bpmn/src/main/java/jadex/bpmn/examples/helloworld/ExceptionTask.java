package jadex.bpmn.examples.helloworld;

import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Simple test task that throws an exception.
 */
public class ExceptionTask extends AbstractTask
{
	public Object doExecute(ITaskContext context)
	{
		throw new RuntimeException("Exception occurred.");
	}
}
