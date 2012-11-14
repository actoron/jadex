package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.task.info.ParameterMetaInfo;
import jadex.bpmn.task.info.TaskMetaInfo;

/**
 *  Writes values to context variables.
 */
public class WriteContextTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance) throws Exception
	{
		if(context.hasParameterValue("name"))
		{
			String name = (String)context.getParameterValue("name");
			Object val = context.getParameterValue("value");
			Object key = context.getParameterValue("key");
			instance.setContextVariable(name, key, val);
		}
		
		for(int i=0; ; i++)
		{
			if(context.hasParameterValue("name"+i))
			{
				String name = (String)context.getParameterValue("name"+i);
				Object val = context.getParameterValue("value"+i);
				Object key = context.getParameterValue("key"+i);
				instance.setContextVariable(name, key, val);
			}
			else
			{
				break;
			}
		}
	}
	
	/**
	 *  Get the meta-info.
	 *  @return The meta-info.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The write context task can be used to write values to context variables.";
		ParameterMetaInfo vnamemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "name", null, "The name of the context variable that is the target of the write operation.");
		ParameterMetaInfo valuemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
				Object.class, "value", null, "The value that is written to the context variable.");
		
		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{vnamemi, valuemi}); 
	}
}
