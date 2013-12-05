package jadex.bpmn.tutorial;

import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.ITaskContext;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bpmn.model.task.annotation.TaskParameter;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

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
		IFuture<IClockService> clockfut	= SServiceProvider.getServiceUpwards(process.getServiceContainer(), IClockService.class);
		clockfut.addResultListener(new IResultListener<IClockService>()
		{
			public void resultAvailable(final IClockService	clock)
			{
				((BpmnInterpreter) process).getComponentAdapter().invokeLater(new Runnable()
				{
					public void run()
					{
						context.setParameterValue("time", Long.valueOf(clock.getTime()));
						ret.setResult(null);
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
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
