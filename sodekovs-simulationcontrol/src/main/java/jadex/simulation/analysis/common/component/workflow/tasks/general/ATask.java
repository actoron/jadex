package jadex.simulation.analysis.common.component.workflow.tasks.general;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.events.task.ATaskObservable;

import java.util.UUID;

public abstract class ATask extends ATaskObservable implements IATask
{
	protected Integer taskNumber;
	protected UUID id = UUID.randomUUID();
	protected MActivity activity;

	public ATask()
	{
	}
	
	@Override
	public UUID getID()
	{
		return id;
	}
	
	public abstract IFuture execute(ITaskContext context, BpmnInterpreter instance);

	@Override
	public MActivity getActivity()
	{
		return activity;
	}
	
	@Override
	public Integer getTaskNumber()
	{
		return taskNumber;
	}
	
	@Override
	public void setTaskNumber(Integer taskNumber)
	{
		this.taskNumber =taskNumber;
	}
}
