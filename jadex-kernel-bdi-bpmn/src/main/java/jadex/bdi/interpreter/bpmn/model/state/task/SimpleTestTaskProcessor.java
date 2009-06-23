package jadex.bdi.interpreter.bpmn.model.state.task;

import jadex.bdi.interpreter.bpmn.model.IBpmnState;
import jadex.bdi.interpreter.bpmn.model.ITaskProcessor;
import jadex.bdi.interpreter.bpmn.model.ITaskProperty;
import jadex.bdi.runtime.IBpmnPlanContext;



public class SimpleTestTaskProcessor implements ITaskProcessor
{
	
	public void execute(IBpmnPlanContext executor, IBpmnState state) 
	{	
		System.out.println("\n\nThis was a simple test task\n\n");
	}

}
