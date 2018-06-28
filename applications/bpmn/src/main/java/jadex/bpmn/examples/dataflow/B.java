package jadex.bpmn.examples.dataflow;

import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bridge.IInternalAccess;

/**
 *  Print out some text stored in variable text.
 */
@Task(parameters=
{
	@TaskParameter(name="a", clazz=String.class, direction=TaskParameter.DIRECTION_IN),
	@TaskParameter(name="b", clazz=String.class, direction=TaskParameter.DIRECTION_OUT),
})
public class B extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		String a = (String)context.getParameterValue("a");
		
		context.setParameterValue("b", a);
		
		System.out.println("got param values: "+a);
	}
}
