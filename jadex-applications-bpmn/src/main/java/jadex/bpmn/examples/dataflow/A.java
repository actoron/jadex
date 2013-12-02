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
		Integer ia = (Integer)context.getParameterValue("a");
		Integer ib = (Integer)context.getParameterValue("b");
		int a = ia!=null? ia.intValue(): 0;
		int b = ib!=null? ib.intValue(): 0;
		
		context.setParameterValue("c", Integer.valueOf(a+b));
		context.setParameterValue("d", ""+a+b);
		
		System.out.println("got param values: "+a+" "+b);
	}
}
