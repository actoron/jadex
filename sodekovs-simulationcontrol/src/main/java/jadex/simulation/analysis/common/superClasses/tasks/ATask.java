package jadex.simulation.analysis.common.superClasses.tasks;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;
import jadex.simulation.analysis.common.superClasses.events.AObservable;
import jadex.simulation.analysis.common.superClasses.events.task.ATaskEvent;
import jadex.simulation.analysis.common.superClasses.service.view.session.subprocess.ASubProcessView;
import jadex.simulation.analysis.common.util.AConstants;

import java.util.UUID;

/**
 * Basic Task Implementation
 * @author 5Haubeck
 *
 */
public class ATask extends AObservable implements IATask
{
	protected Integer taskNumber;
	protected String id = UUID.randomUUID().toString();
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
	public String getID()
	{
		return id;
	}

	public IFuture execute(ITaskContext context, BpmnInterpreter instance)
	{
		activity = context.getActivity();
		this.context = context;
		this.instance = instance;
		((ASubProcessView) instance.getContextVariable("subProcessView")).registerTask(this, view);
		notify(new ATaskEvent(this, context, instance, AConstants.TASK_LÄUFT));
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

	public IATaskView getView()
	{	
		return view;
	}

	@Override
	public IFuture<Void> cancel(BpmnInterpreter instance) {
		notify(new ATaskEvent(this, null, instance, AConstants.TASK_ABBRUCH));
		return new Future();
	}
}
