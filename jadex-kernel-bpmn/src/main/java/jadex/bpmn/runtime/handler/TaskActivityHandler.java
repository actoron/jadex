package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ProcessThread;
import jadex.commons.future.IResultListener;

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
		if (thread.isCanceled())
			return;
		
		Class taskimpl = activity.getClazz() != null? activity.getClazz().getType(instance.getClassLoader(), instance.getModel().getAllImports()) : null;
		if(taskimpl!=null)
		{
//			thread.setWaitingState(ProcessThread.WAITING_FOR_TASK);
			thread.setWaiting(true);
			try
			{
				ITask task = (ITask)taskimpl.newInstance();
				thread.setTask(task);
				thread.setCanceled(false);
				task.execute(thread, instance).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						if(!thread.isCanceled())
							instance.notify(activity, thread, null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
						if(!thread.isCanceled())
						{
							thread.setException(exception);
							instance.notify(activity, thread, null);
						}	
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
	
	/**
	 *  Cancel an activity.
	 *  @param activity	The activity to execute.
	 *  @param instance	The process instance.
	 *  @param thread The process thread.
	 *  @param info The info object.
	 */

	public void cancel(MActivity activity, BpmnInterpreter instance, ProcessThread thread)
	{
		thread.setCanceled(true);
		ITask task = thread.getTask();
		if (task != null)
			task.cancel(instance);
	}
}
