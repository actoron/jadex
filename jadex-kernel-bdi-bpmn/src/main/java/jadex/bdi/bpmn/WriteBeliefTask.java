package jadex.bdi.bpmn;

import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Write a belief (set) value.
 *  The belief(set) is specified by the 'belief(set)name' belief.
 *  The value is specified by the 'value' belief.
 *  For belief sets a boolean 'add' belief can be specified to distinguish
 *  between value addition (default) and removal.
 */
public class WriteBeliefTask extends AbstractTask
{
	public void doExecute(ITaskContext context, IProcessInstance instance)
	{
		BpmnPlanBodyInstance inst = (BpmnPlanBodyInstance)instance;
		Object value = context.getParameterValue("value");
		
		if(context.hasParameterValue("beliefname"))
		{
			String name = (String)context.getParameterValue("beliefname");
			inst.getBeliefbase().getBelief(name).setFact(value);
		}
		else if(context.hasParameterValue("beliefsetname"))
		{
			String name = (String)context.getParameterValue("beliefname");
			if(!context.hasParameterValue("add") || ((Boolean)context.getParameterValue("add")).booleanValue())
			{
				inst.getBeliefbase().getBeliefSet(name).addFact(value);
			}
			else
			{
				inst.getBeliefbase().getBeliefSet(name).removeFact(value);
			}
		}
		else
		{
			throw new RuntimeException("Belief(set)name no specified: "+context);
		}
	}
}
