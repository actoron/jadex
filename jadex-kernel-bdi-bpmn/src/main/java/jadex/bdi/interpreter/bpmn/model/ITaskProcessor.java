package jadex.bdi.interpreter.bpmn.model;

import jadex.bdi.runtime.IBpmnPlanContext;

public interface ITaskProcessor
{

	public void execute(IBpmnPlanContext executor, IBpmnState state);

}
