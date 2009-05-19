package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;

/**
 *  Interface for a task of an object.
 */
public interface IObjectTask
{
	/**
	 *  This method will be executed by the object before the task gets added to
	 *  the execution queue.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 */
	public void start(/*IEnvironmentSpace space,*/ ISpaceObject obj);
	
	/**
	 *  This method will be executed by the object before the task is removed
	 *  from the execution queue.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 */
	public void shutdown(/*IEnvironmentSpace space,*/ ISpaceObject obj);

	/**
	 *  Executes the task.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, IVector1 progress);
}
