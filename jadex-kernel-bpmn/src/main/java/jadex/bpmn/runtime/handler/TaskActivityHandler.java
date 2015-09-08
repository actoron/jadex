package jadex.bpmn.runtime.handler;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.exttask.ExternalTaskWrapper;
import jadex.bpmn.runtime.task.PojoTaskWrapper;
import jadex.bridge.IInternalAccess;
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
	public void execute(final MActivity activity, final IInternalAccess instance, final ProcessThread thread)
	{
		if(thread.isCanceled())
			return;
		
		Class<?> taskimpl = activity.getClazz()!=null? activity.getClazz().getType(instance.getClassLoader(), instance.getModel().getAllImports()) : null;
		if(taskimpl!=null)
		{
//			thread.setWaitingState(ProcessThread.WAITING_FOR_TASK);
			thread.setWaiting(true);
			try
			{
				Object tmp = taskimpl.newInstance();
				if(!(tmp instanceof ITask))
				{
					tmp = new PojoTaskWrapper(tmp, instance, thread, activity.getComponentInjections(instance.getClassLoader()),
						activity.getArgumentInjections(instance.getClassLoader()), activity.getResultInjections(instance.getClassLoader()));
				}
				ITask task = (ITask)tmp;
				
				String ext = activity.getPropertyValueString("external");
				if(ext!=null && Boolean.parseBoolean(ext))
				{
					task = new ExternalTaskWrapper(task);
				}
				
				thread.setTask(task);
				thread.setCanceled(false);
				
				task.execute(thread, instance).addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						if(!thread.isCanceled())
							getBpmnFeature(instance).notify(activity, thread, null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						exception.printStackTrace();
						if(!thread.isCanceled())
						{
							thread.setException(exception);
							getBpmnFeature(instance).notify(activity, thread, null);
						}	
					}
				});
			}
			catch(Exception e)
			{
				if(thread.getException()==null)
				{
					thread.setException(e);
					getBpmnFeature(instance).notify(activity, thread, null);
				}
				else
				{
					// Hack!!! Rethrow exception when task.execute() is synchronous
					// and instance.notify() throws exception due to no suitable exception handlers in BPMN.
					throw thread.getException() instanceof RuntimeException
						? (RuntimeException)thread.getException() : new RuntimeException(thread.getException());
				}
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

	public void cancel(MActivity activity, IInternalAccess instance, ProcessThread thread)
	{
		thread.setCanceled(true);
		ITask task = thread.getTask();
		if(task != null)
		{
			task.cancel(instance);
		}
	}
}
