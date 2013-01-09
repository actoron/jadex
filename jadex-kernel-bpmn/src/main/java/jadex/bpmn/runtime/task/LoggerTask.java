package jadex.bpmn.runtime.task;

import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;
import jadex.bridge.IInternalAccess;

import java.util.logging.Level;

/**
 *  Log some text stored in variable text.
 */
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
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The logger task can be used for logging some text.";
		ParameterMetaInfo textmi	= new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
				String.class, "text", null, "The text parameter should contain the text to be logged.");
		ParameterMetaInfo levelmi	= new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
				Level.class, "level", "java.util.logging.Level.INFO", "The logging level (e.g. INFO, WARNING, SEVERE).");
		
		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{textmi, levelmi}); 
	}
}
