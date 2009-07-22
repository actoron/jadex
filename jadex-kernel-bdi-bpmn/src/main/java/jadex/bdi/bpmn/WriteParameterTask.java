package jadex.bdi.bpmn;

import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Write a parameter (set) value.
 *  The parameter(set) is specified by the 'parameter(set)name' parameter.
 *  The value is specified by the 'value' parameter.
 *  For parameter sets a boolean 'add' parameter can be specified to distinguish
 *  between value addition (default) and removal.
 */
public class WriteParameterTask extends AbstractTask
{
	public void doExecute(ITaskContext context, IProcessInstance instance)
	{
		BpmnPlanBodyInstance inst = (BpmnPlanBodyInstance)instance;
		Object value = context.getParameterValue("value");
		
		if(context.hasParameterValue("parametername"))
		{
			String name = (String)context.getParameterValue("parametername");
			inst.getParameter(name).setValue(value);
		}
		else if(context.hasParameterValue("parametersetname"))
		{
			String name = (String)context.getParameterValue("parametersetname");
			if(!context.hasParameterValue("add") || ((Boolean)context.getParameterValue("add")).booleanValue())
			{
				inst.getParameterSet(name).addValue(value);
			}
			else
			{
				inst.getParameterSet(name).removeValue(value);
			}
		}
		else
		{
			throw new RuntimeException("Parameter(set)name no specified: "+context);
		}
	}
}
