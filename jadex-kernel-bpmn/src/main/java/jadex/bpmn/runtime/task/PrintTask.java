package jadex.bpmn.runtime.task;

import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskArgument;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.IInternalAccess;

/**
 *  Print out some text stored in variable text.
 */
@Task(description="The print task can be used for printing out a text on the console.",	parameters=
	@TaskParameter(name="text", clazz=String.class, direction=TaskParameter.DIRECTION_IN,
		description="The text parameter should contain the text to be printed."))
public class PrintTask extends AbstractTask
{	
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		String text = (String)context.getParameterValue("text");
		System.out.println(text);
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The print task can be used for printing out a text on the console.";
		ParameterMetaInfo textmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "text", null, "The text parameter should contain the text to be printed.");
		
		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{textmi}); 
	}
}
