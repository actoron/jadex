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
			String val = (String)context.getParameterValue("value");
			context.setResultValue(name, val);
		}
		
		for(int i=0; ; i++)
		{
			if(context.hasParameterValue("name"+i))
			{
				String name = (String)context.getParameterValue("name"+i);
				String val = (String)context.getParameterValue("value"+i);
				context.setResultValue(name, val);
			}
			else
			{
				break;
			}
		}
	}
}
