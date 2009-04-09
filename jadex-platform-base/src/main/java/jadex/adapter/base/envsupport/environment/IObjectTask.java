package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.bridge.IClock;

/**
 *  Interface for a task of an object.
 */
public interface IObjectTask
{
	/**
	 *  This method will be executed by the object before the task gets added to
	 *  the execution queue.
	 *  @param the environment object that is executing the task
	 */
	public void start(ISpaceObject obj);
	
	/**
	 *  This method will be executed by the object before the task is removed
	 *  from the execution queue.
	 *  @param the environment object that is executing the task
	 */
	public void shutdown(ISpaceObject obj);

	/**
	 *  Executes the task.
	 *  @param progress some indicator of progress (may be time, step number or set to 0 if not needed)
	 *  @param obj to the environment object that is executing the task
	 */
	public void execute(IVector1 progress, ISpaceObject obj);

	/**
	 *  Returns the ID of the task.
	 *  @return ID of the task.
	 */
	public Object getId();
}
