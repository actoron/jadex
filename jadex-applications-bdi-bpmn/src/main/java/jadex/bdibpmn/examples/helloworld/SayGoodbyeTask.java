package jadex.bdibpmn.examples.helloworld;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bpmnbdi.BpmnPlanBodyInstance;

/**
 *  Print goodbye on the console and print parameter 'y'.
 */
public class SayGoodbyeTask	extends AbstractTask
{
	public void doExecute(ITaskContext context, BpmnInterpreter instance)
	{
		System.out.println("Goodbye BPMN world!");
		int	y = ((Number)context.getParameterValue("y")).intValue();
		System.out.println("Parameter y: "+y);
		
		if(instance instanceof BpmnPlanBodyInstance)
			((BpmnPlanBodyInstance)instance).killAgent();
	}
}
