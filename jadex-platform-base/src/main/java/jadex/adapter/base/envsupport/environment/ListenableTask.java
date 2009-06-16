package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IResultListener;

/**
 *  A task that informs about its completion or abortion.
 *  Subclasses should override doExecute() and call taskFinished() when the task
 *  is completed.
 */
public abstract class ListenableTask extends SimplePropertyObject implements IObjectTask
{
	//-------- constants --------
	
	/** Property for listener parameter. */
	public static final String PROPERTY_LISTENER = "listener";
	
	//-------- attributes --------
	
	/** The result listener. */
//	protected IResultListener	listener;
	
	/** The result of the task (if any). */
	protected Object	result;
	
	/** The exception occurred during execution (if any). */
	protected Exception	exception;
	
	//-------- constructors ---------
	
	/**
	 *  Create a new listenable task.
	 * /
	public ListenableTask()//IResultListener listener)
	{
//		this.listener	= listener;
//		this.listener = (IResultListener)getProperty(PROPERTY_LISTENER);
	}*/
	
	//-------- IObjectTask --------
	
	/**
	 *  Get the objects id.
	 *  @return The object id.
	 */
	public Object getId()
	{
		return getProperty(IObjectTask.ID);
	}
	
	/**
	 *  This method will be executed by the object before the task gets added to
	 *  the execution queue.
	 *  Empty default implementation that can be replaced by subclasses.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 */
	public void start(/*IEnvironmentSpace space,*/ ISpaceObject obj)
	{
	}
	
	/**
	 *  This method will be executed by the object before the task is removed
	 *  from the execution queue.
	 *  Notifies the listener and hence should be called when overridden in subclasses.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 */
	public void shutdown(/*IEnvironmentSpace space,*/ ISpaceObject obj)
	{
		IResultListener listener = (IResultListener)getProperty(PROPERTY_LISTENER);
		if(exception!=null)
			listener.exceptionOccurred(exception);
		else
			listener.resultAvailable(result);
	}

	/**
	 *  Executes the task.
	 *  Handles exceptions. Subclasses should implement doExecute() instead.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, IVector1 progress)
	{
		try
		{
			doExecute(space, obj, progress);
		}
		catch(Exception e)
		{
			this.exception	= e;
			space.removeObjectTask(this.getId(), obj.getId());
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Mark the task as finished.
	 *  Stops the task and informs the listener.
	 *  @param obj	The environment object that is executing the task.
	 *  @param result	A result (if any).
	 */
	public void	taskFinished(IEnvironmentSpace space, ISpaceObject obj, Object result)
	{
		this.result	= result;
		space.removeObjectTask(this.getId(), obj.getId());
	}
	
	//-------- template methods --------
	
	/**
	 *  Executes the task.
	 *  Needs to be implemented by subclasses.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public abstract void doExecute(IEnvironmentSpace space, ISpaceObject obj, IVector1 progress);
}
