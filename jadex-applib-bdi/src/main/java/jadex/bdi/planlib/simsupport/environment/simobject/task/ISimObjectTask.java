package jadex.bdi.planlib.simsupport.environment.simobject.task;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

public interface ISimObjectTask
{
	/** This method will be executed by the object before
	 *  the task gets added to the execution queue.
	 *  
	 *  @param object the object that is executing the task
	 */
	public void start(SimObject object);
	
	/** This method will be executed by the object before
	 *  the task is removed from the execution queue.
	 *  
	 *  @param object the object that is executing the task
	 */
	public void shutdown(SimObject object);
	
	/** Executes the task
	 * 
	 *  @param deltaT time passed
	 *  @param object the object that is executing the task
	 */
	public void execute(IVector1 deltaT, SimObject object);
	
	/** Returns the name of the task.
	 * 
	 *  @return name of the task.
	 */
	public String getName();
}
