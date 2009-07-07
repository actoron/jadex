package jadex.bpmn.runtime;

import jadex.bpmn.model.MActivity;
import jadex.commons.concurrent.IResultListener;

/**
 *  Abstract handler for tasks.
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
			try
			{
				ITask task = (ITask)taskimpl.newInstance();
				thread.setWaiting(true);
				task.execute(thread, new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						TaskActivityHandler.this.notify(activity, instance, thread);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						TaskActivityHandler.this.notify(activity, instance, thread);
						exception.printStackTrace();
						System.out.println("Exception during task execution: "+exception);
					}
				});
			}
			catch(Exception e)
			{
				e.printStackTrace();
				throw new RuntimeException("Task lead to exception: "+e);
			}
		}
		else
		{
			super.execute(activity, instance, thread);
		}
	}
}
