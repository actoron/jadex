package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
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
	 */
	public void execute(final MActivity activity, final BpmnInstance instance, final ProcessThread thread)
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
						TaskActivityHandler.this.notify(activity, instance, thread);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						thread.setException(exception);
						TaskActivityHandler.this.notify(activity, instance, thread);
					}
				});
			}
			catch(Exception e)
			{
				thread.setException(e);
				TaskActivityHandler.this.notify(activity, instance, thread);
			}
		}
		else
		{
			super.execute(activity, instance, thread);
		}
	}
}
