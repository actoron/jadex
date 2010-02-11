package jadex.bpmn.runtime.task;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;

/**
 *  Store results of the process.
 */
public class StoreResultsTask extends AbstractTask
{
	/**
	 *  Execute the task.
	 */
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		if(context.hasParameterValue("name"))
		{
			String name = (String)context.getParameterValue("name");
			Object val = context.getParameterValue("value");
			context.setResultValue(name, val);
		}
		
		for(int i=0; ; i++)
		{
			if(context.hasParameterValue("name"+i))
			{
				String name = (String)context.getParameterValue("name"+i);
				Object val = context.getParameterValue("value"+i);
				context.setResultValue(name, val);
			}
			else
			{
				break;
			}
		}
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static TaskMetaInfo getMetaInfo()
	{
		String desc = "The store results task can be used for storing values as process results. As" +
			"parameters a name, value pair or if more than one results an arbitrary number of name, value" +
			"pairs with a postfix number can be used (e.g. name0 and value0, name1 and value1, etc.)";
		ParameterMetaInfo namemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "name", null, "The name parameter identifies the name of the result parameter.");
		ParameterMetaInfo valuemi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "value", null, "The value parameter identifies the value of the result parameter belonging to the name.");
		ParameterMetaInfo namesmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			String.class, "name0[..n]", null, "The name0[..n] parameter(s) identify the name(s) of the result parameter(s).");
		ParameterMetaInfo valuesmi = new ParameterMetaInfo(ParameterMetaInfo.DIRECTION_IN, 
			Object.class, "value0[..n]", null, "The value0[..n] parameter(s) identify the value(s) of the result parameter(s) belonging to the name(s).");

		
		return new TaskMetaInfo(desc, new ParameterMetaInfo[]{namemi, valuemi, namesmi, valuesmi}); 
	}
}
