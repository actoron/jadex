package jadex.bpmn.examples.helloworld;

import jadex.bdi.bpmn.BpmnPlanBodyInstance;
import jadex.bpmn.runtime.IProcessInstance;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;

/**
 *  Print goodbye on the console and print parameter 'y'.
 */
public class SayGoodbyeTask	extends AbstractTask
{
	public Object doExecute(ITaskContext context, IProcessInstance instance)
	{
		System.out.println("Goodbye BPMN world!");
		int	y = ((Number)context.getParameterValue("y")).intValue();
		System.out.println("Parameter y: "+y);
		
		if(instance instanceof BpmnPlanBodyInstance)
			((BpmnPlanBodyInstance)instance).killAgent();
		return null;
	}
}
