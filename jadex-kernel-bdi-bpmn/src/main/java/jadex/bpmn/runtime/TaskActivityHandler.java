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
	public void execute(MActivity activity, BpmnInstance instance, final ProcessThread thread)
	{
		thread.setWaiting(true);
		Class taskimpl = (Class)activity.getPropertyValue("class");
		try
		{
			ITask task = (ITask)taskimpl.newInstance();
			thread.setWaiting(true);
			task.execute(thread, new IResultListener()
			{
				
				public void resultAvailable(Object result)
				{
					notify();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					notify();
					System.out.println("Exception during task execution: "+exception);
				}
			});
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("");
		}
	}
}
