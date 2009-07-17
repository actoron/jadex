package jadex.bpmn.examples.helloworld;

import jadex.bdi.bpmn.BpmnPlanBodyInstance;
import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Write a belief value task.
 */
public class WriteBeliefTask extends AbstractTask
{
	public Object doExecute(ITaskContext context, IProcessInstance instance)
	{
		BpmnPlanBodyInstance inst = (BpmnPlanBodyInstance)instance;
		
		String belname = (String)context.getParameterValue("beliefname");
		Object value = context.getParameterValue("value");
		
		System.out.println("Setting belief value: "+belname+" "+value);
		
		inst.getBeliefbase().getBelief(belname).setFact(value);
		
		return null;
	}
}
