package jadex.bpmn.tutorial;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  A task that provides the current platform time in the 'time' parameter.
 */
public class GetTimeTask	 implements ITask
{
	/**
	 *  Execute the task.
	 */
	public IFuture<Void> execute(final ITaskContext context, final BpmnInterpreter process)
	{
		final Future<Void> ret = new Future<Void>();
		IFuture<IClockService> clockfut	= SServiceProvider.getServiceUpwards(process.getServiceContainer(), IClockService.class);
		clockfut.addResultListener(new IResultListener<IClockService>()
		{
			public void resultAvailable(final IClockService	clock)
			{
				process.getComponentAdapter().invokeLater(new Runnable()
				{
					public void run()
					{
						context.setParameterValue("time", new Long(clock.getTime()));
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
	public IFuture<Void> cancel(final BpmnInterpreter instance)
	{
		return IFuture.DONE;
	}
}
