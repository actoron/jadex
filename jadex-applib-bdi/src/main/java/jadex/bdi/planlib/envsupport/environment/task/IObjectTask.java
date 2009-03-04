package jadex.bdi.planlib.envsupport.environment.task;

import jadex.bdi.planlib.envsupport.environment.IEnvironmentObject;
import jadex.bdi.planlib.envsupport.math.IVector1;
import jadex.bridge.IClock;


public interface IObjectTask
{
	/**
	 * This method will be executed by the object before the task gets added to
	 * the execution queue.
	 * 
	 * @param the environment object that is executing the task
	 */
	public void start(IEnvironmentObject obj);
	
	/**
	 * This method will be executed by the object before the task is removed
	 * from the execution queue.
	 * 
	 * @param the environment object that is executing the task
	 */
	public void shutdown(IEnvironmentObject obj);

	/**
	 * Executes the task.
	 * 
	 * @param clock the clock
	 * @param deltaT time passed
	 * @param obj to the environment object that is executing the task
	 */
	public void execute(IClock clock, IVector1 deltaT, IEnvironmentObject obj);

	/**
	 * Returns the ID of the task.
	 * 
	 * @return ID of the task.
	 */
	public Object getId();
}
