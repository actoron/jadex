package jadex.bdibpmn.examples.helloworld;

import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bridge.IInternalAccess;

/**
 *  Simple test task that throws an exception.
 */
public class ExceptionTask extends AbstractTask
{
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		throw new RuntimeException("Exception occurred.");
	}
}
