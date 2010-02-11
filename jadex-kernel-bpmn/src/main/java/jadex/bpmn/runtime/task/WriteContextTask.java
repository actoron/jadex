package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;

/**
 *  Writes values to context variables.
 */
public class WriteContextTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
			throws Exception
	{
		if(context.hasParameterValue("variablename"))
		{
			String name = (String)context.getParameterValue("variablename");
			Object val = context.getParameterValue("value");
			instance.setContextVariable(name, val);
		}
		
		for(int i=0; ; i++)
		{
			if(context.hasParameterValue("variablename"+i))
			{
				String name = (String)context.getParameterValue("variablename"+i);
				Object val = context.getParameterValue("value"+i);
				instance.setContextVariable(name, val);
			}
			else
			{
				break;
			}
		}
	}
	
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The print task can be used to write values to context variables.";
		ParameterMetaInfo vnamemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "variablename", null, "The name of the context variable that is the target of the write operation.");
		ParameterMetaInfo valuemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
				Object.class, "value", null, "The value that is written to the context variable.");
		
		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{vnamemi, valuemi}); 
	}
}
