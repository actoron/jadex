package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInstance;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.ThreadContext;
import jadex.commons.concurrent.IResultListener;

/**
 *  Handler for (external) tasks.
 */
public class TaskActivityHandler extends DefaultActivityHandler
{
	/**
	 *  Execute an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread	The process thread.
	 *  @param context	The thread context.
	 */
	public void execute(final MActivity activity, final BpmnInstance instance, final ProcessThread thread, final ThreadContext context)
	{
		Class taskimpl = (Class)activity.getPropertyValue("class");
		if(taskimpl!=null)
		{
			thread.setWaiting(true);
			try
			{
				ITask task = (ITask)taskimpl.newInstance();
				task.execute(thread, new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						TaskActivityHandler.this.notify(activity, instance, thread, context);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						thread.setException(exception);
						TaskActivityHandler.this.notify(activity, instance, thread, context);
					}
				});
			}
			catch(Exception e)
			{
				thread.setException(e);
				TaskActivityHandler.this.notify(activity, instance, thread, context);
			}
		}
		else
		{
			super.execute(activity, instance, thread, context);
		}
	}
}
