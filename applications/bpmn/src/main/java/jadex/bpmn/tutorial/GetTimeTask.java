package jadex.bpmn.tutorial;

import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  A task that provides the current platform time in the 'time' parameter.
 */
@Task(description="Task that delivers the current time in parameter 'time'.",
	parameters=@TaskParameter(name="time", clazz=Long.class, direction=TaskParameter.DIRECTION_OUT))
public class GetTimeTask	 implements ITask
{
	/**
	 *  Execute the task.
	 */
	public IFuture<Void> execute(final ITaskContext context, final IInternalAccess process)
	{
		final Future<Void> ret = new Future<Void>();
		IClockService clock = process.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM));
		context.setParameterValue("time", Long.valueOf(clock.getTime()));
		return ret;
	}
	
	/**
	 *  Compensate in case the task is canceled.
	 *  @return	To be notified, when the compensation has completed.
	 */
	public IFuture<Void> cancel(final IInternalAccess instance)
	{
		return IFuture.DONE;
	}
}
