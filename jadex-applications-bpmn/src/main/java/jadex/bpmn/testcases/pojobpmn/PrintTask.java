package jadex.bpmn.testcases.pojobpmn;

import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskArgument;
import jadex.bpmn.model.task.annotation.TaskBody;
import jadex.bpmn.model.task.annotation.TaskComponent;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bridge.IInternalAccess;

/**
 *  Print out some text stored in variable text.
 */
@Task(description="The print task can be used for printing out a text on the console.",	parameters=
	@TaskParameter(name="text", clazz=String.class, direction=TaskParameter.DIRECTION_IN,
	description="The text parameter should contain the text to be printed."))
public class PrintTask
{
	/** Injected argument. */
	@TaskArgument
	protected String text;
	
	/** The component. */
	@TaskComponent
	protected IInternalAccess ia;

	
	/**
	 *  Execute the task.
	 */
	@TaskBody
//	public void execute(ITaskContext context, @ParameterInfo("text") String text)
	public void execute(String text)
	{
		System.out.println(text+" "+this.text+" "+ia);
	}
	
}