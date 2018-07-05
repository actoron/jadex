package jadex.bpmn.runtime.task;

import jadex.bpmn.features.IBpmnComponentFeature;
import jadex.bpmn.features.IInternalBpmnComponentFeature;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bridge.IInternalAccess;

/**
 *  Writes values to context variables.
 */
@Task(description="The write context task can be used to write values to context variables.", parameters=
	{
		@TaskParameter(name="name", clazz=String.class, direction=TaskParameter.DIRECTION_IN,
			description="The name of the context variable that is the target of the write operation."),
		@TaskParameter(name="value", clazz=Object.class, direction=TaskParameter.DIRECTION_IN,
			description="The value that is written to the context variable."),
		@TaskParameter(name="key", clazz=Object.class, direction=TaskParameter.DIRECTION_IN,
			description="The optional key if should be put in map.")
	}
)
public class WriteContextTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, IInternalAccess instance) throws Exception
	{
		if(context.hasParameterValue("name"))
		{
			String name = (String)context.getParameterValue("name");
			Object val = context.getParameterValue("value");
			Object key = context.getParameterValue("key");
			((IInternalBpmnComponentFeature)instance.getFeature(IBpmnComponentFeature.class)).setContextVariable(name, key, val);
		}
		
		for(int i=0; ; i++)
		{
			if(context.hasParameterValue("name"+i))
			{
				String name = (String)context.getParameterValue("name"+i);
				Object val = context.getParameterValue("value"+i);
				Object key = context.getParameterValue("key"+i);
				((IInternalBpmnComponentFeature)instance.getFeature(IBpmnComponentFeature.class)).setContextVariable(name, key, val);
			}
			else
			{
				break;
			}
		}
	}
	
//	/**
//	 *  Get the meta-info.
//	 *  @return The meta-info.
//	 */
//	public static TaskMetaInfo getMetaInfo()
//	{
//		String desc = "The write context task can be used to write values to context variables.";
//		ParameterMetaInfo vnamemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//			String.class, "name", null, "The name of the context variable that is the target of the write operation.");
//		ParameterMetaInfo valuemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
//				Object.class, "value", null, "The value that is written to the context variable.");
//		
//		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{vnamemi, valuemi}); 
//	}
}
