package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;

/**
 *  Print out some text stored in variable text.
 */
public class PrintTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
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
