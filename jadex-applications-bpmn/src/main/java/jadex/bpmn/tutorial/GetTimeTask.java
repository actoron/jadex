package jadex.bpmn.tutorial;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.clock.IClockService;

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
		SServiceProvider.getService(process.getServiceProvider(), IClockService.class)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
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
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		return ret;
	}
}
