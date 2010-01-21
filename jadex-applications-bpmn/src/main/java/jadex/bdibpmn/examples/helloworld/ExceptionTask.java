package jadex.bdibpmn.examples.helloworld;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Simple test task that throws an exception.
 */
public class ExceptionTask extends AbstractTask
{
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		throw new RuntimeException("Exception occurred.");
	}
}
