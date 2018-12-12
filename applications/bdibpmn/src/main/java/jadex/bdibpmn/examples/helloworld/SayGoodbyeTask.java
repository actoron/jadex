package jadex.bdibpmn.examples.helloworld;

import jadex.bdibpmn.BpmnPlanBodyInstance;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.runtime.task.AbstractTask;
import jadex.bridge.IInternalAccess;

/**
 *  Print goodbye on the console and print parameter 'y'.
 */
public class SayGoodbyeTask	extends AbstractTask
{
	public void doExecute(ITaskContext context, IInternalAccess instance)
	{
		System.out.println("Goodbye BPMN world!");
		int	y = ((Number)context.getParameterValue("y")).intValue();
		System.out.println("Parameter y: "+y);
		
		if(instance instanceof BpmnPlanBodyInstance)
			((BpmnPlanBodyInstance)instance).killAgent();
	}
}
