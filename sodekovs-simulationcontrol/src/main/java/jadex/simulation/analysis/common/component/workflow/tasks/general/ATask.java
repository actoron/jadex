package jadex.simulation.analysis.common.component.workflow.tasks.general;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.IFuture;

import java.util.UUID;

public abstract class ATask extends ATaskObservable implements IATask
{
	protected UUID id = UUID.randomUUID();
	@Override
	public UUID getID()
	{
		return id;
	}
	
	public abstract IFuture execute(ITaskContext context, BpmnInterpreter instance);
	
}
