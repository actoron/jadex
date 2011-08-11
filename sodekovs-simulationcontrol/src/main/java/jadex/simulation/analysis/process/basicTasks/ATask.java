package jadex.simulation.analysis.process.basicTasks;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.events.task.ATaskEvent;
import jadex.simulation.analysis.common.events.task.ATaskObservable;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.basic.view.session.subprocess.ASubProcessView;

import java.util.UUID;

public class ATask extends ATaskObservable implements IATask
{
	protected Integer taskNumber;
	protected UUID id = UUID.randomUUID();
	protected MActivity activity;
	protected ITaskContext context;
	protected BpmnInterpreter instance;
	protected ISuspendable susThread = new ThreadSuspendable(this);
	protected IATaskView view;

	public ATask()
	{
		view = new ATaskView(this);
	}

	@Override
	public IFuture compensate(BpmnInterpreter instance)
	{
		taskChanged(new ATaskEvent(this, null, instance, AConstants.TASK_ABBRUCH));
		return new Future();
	}

	@Override
	public UUID getID()
	{
		return id;
	}

	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		activity = context.getActivity();
		this.context = context;
		this.instance = instance;
		((ASubProcessView) instance.getContextVariable("subProcessView")).registerTask(this, view);
		taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_LÄUFT));
		return new Future(null);
	}

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
		this.taskNumber = taskNumber;
	}

	@Override
	public void userInteractionRequired(Boolean user)
	{
		if (user == true)
		{
			taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_LÄUFT));
		}
		else if (user == false)
		{
			taskChanged(new ATaskEvent(this, context, instance, AConstants.TASK_USER));
		}
	}

	public IATaskView getView()
	{	
		return view;
	}
}
