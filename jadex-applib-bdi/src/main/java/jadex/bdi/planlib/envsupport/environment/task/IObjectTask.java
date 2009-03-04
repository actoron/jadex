package jadex.bdi.planlib.envsupport.environment.task;

import jadex.bdi.planlib.envsupport.environment.IEnvironmentObject;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;


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
	 * @param deltaT time passed
	 * @param access to the environment object that is executing the task
	 */
	public void execute(IVector1 deltaT, IEnvironmentObject access);

	/**
	 * Returns the ID of the task.
	 * 
	 * @return ID of the task.
	 */
	public Object getId();
}
