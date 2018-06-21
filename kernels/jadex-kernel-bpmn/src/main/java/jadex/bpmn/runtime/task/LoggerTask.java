package jadex.bpmn.runtime.task;

import java.util.logging.Level;

import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bridge.IInternalAccess;

/**
 *  Log some text stored in variable text.
 */
@Task(description="The logger task can be used for logging some text.",	parameters={
	@TaskParameter(name="text", clazz=String.class, direction=TaskParameter.DIRECTION_IN,
		description="The text parameter should contain the text to be logged."),
	@TaskParameter(name="level", clazz=Level.class, direction=TaskParameter.DIRECTION_IN,
		initialvalue="java.util.logging.Level.INFO",
		description="The logging level (e.g. INFO, WARNING, SEVERE).")
})
public class LoggerTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		String	text	= (String)context.getParameterValue("text");
		Level	level	= (Level)context.getParameterValue("level");
		instance.getLogger().log(level, text);
	}
	
	//-------- static methods --------
	
//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static TaskMetaInfo getMetaInfo()
//	{
//		String desc = "The logger task can be used for logging some text.";
//		ParameterMetaInfo textmi	= new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//				String.class, "text", null, "The text parameter should contain the text to be logged.");
//		ParameterMetaInfo levelmi	= new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//				Level.class, "level", "java.util.logging.Level.INFO", "The logging level (e.g. INFO, WARNING, SEVERE).");
//		
//		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{textmi, levelmi}); 
//	}
}
