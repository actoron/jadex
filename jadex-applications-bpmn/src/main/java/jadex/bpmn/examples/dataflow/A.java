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
	@TaskParameter(name="a", clazz=int.class, direction=TaskParameter.DIRECTION_IN),
	@TaskParameter(name="b", clazz=int.class, direction=TaskParameter.DIRECTION_IN),
	@TaskParameter(name="c", clazz=int.class, direction=TaskParameter.DIRECTION_OUT),
	@TaskParameter(name="d", clazz=String.class, direction=TaskParameter.DIRECTION_OUT),
})
public class A extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		int a = ((Integer)context.getParameterValue("a")).intValue();
		int b = ((Integer)context.getParameterValue("b")).intValue();
		
		context.setParameterValue("c", new Integer(a+b));
		context.setParameterValue("d", ""+a+b);
		
		System.out.println("got param values: "+a+" "+b);
	}
}
