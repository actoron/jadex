package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITask;
import jadex.bpmn.runtime.ProcessThread;
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
	public void execute(final MActivity activity, final BpmnInterpreter instance, final ProcessThread thread)
	{
//		Class taskimpl = (Class)getPropertyValue(activity, instance, thread, "class");
		Class taskimpl = (Class)thread.getPropertyValue("class");
		if(taskimpl!=null)
		{
//			thread.setWaitingState(ProcessThread.WAITING_FOR_TASK);
			thread.setWaiting(true);
			try
			{
				ITask task = (ITask)taskimpl.newInstance();
				task.execute(thread, instance, new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						instance.notify(activity, thread, null);
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						thread.setException(exception);
						instance.notify(activity, thread, null);
					}
				});
			}
			catch(Exception e)
			{
				thread.setException(e);
				instance.notify(activity, thread, null);
			}
		}
		else
		{
			super.execute(activity, instance, thread);
		}
		
	}
}
