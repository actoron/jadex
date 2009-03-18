package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.bridge.IClock;


public interface IObjectTask
{
	/**
	 * This method will be executed by the object before the task gets added to
	 * the execution queue.
	 * 
	 * @param the environment object that is executing the task
	 */
	public void start(ISpaceObject obj);
	
	/**
	 * This method will be executed by the object before the task is removed
	 * from the execution queue.
	 * 
	 * @param the environment object that is executing the task
	 */
	public void shutdown(ISpaceObject obj);

	/**
	 * Executes the task.
	 * 
	 * @param time the current time
	 * @param deltaT time passed
	 * @param obj to the environment object that is executing the task
	 */
	public void execute(long time, IVector1 deltaT, ISpaceObject obj);

	/**
	 * Returns the ID of the task.
	 * 
	 * @return ID of the task.
	 */
	public Object getId();
}
