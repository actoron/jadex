package jadex.bpmn.tutorial;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
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
	public IFuture execute(final ITaskContext context, final BpmnInterpreter process)
	{
		final Future ret = new Future();
		SServiceProvider.getService(process.getServiceContainer(), IClockService.class)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IClockService	clock	= (IClockService)result;
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
}
