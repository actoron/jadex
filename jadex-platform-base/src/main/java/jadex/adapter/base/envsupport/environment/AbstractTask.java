package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.commons.SimplePropertyObject;

/**
 *  Empty default implementation for object tasks.
 */
public abstract class AbstractTask extends SimplePropertyObject implements IObjectTask
{
	//-------- IObjectTask interface --------
	
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
	}
}
