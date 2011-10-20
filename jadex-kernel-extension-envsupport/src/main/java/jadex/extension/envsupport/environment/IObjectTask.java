package jadex.extension.envsupport.environment;

import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.IPropertyObject;

/**
 *  Interface for a task of an object.
 */
public interface IObjectTask extends IPropertyObject
{
	//-------- constants --------
	
	/** The property for holding the task id. */
	public static final String PROPERTY_ID = "task_id";
	
	//-------- methods --------
	
	/**
	 *  This method will be executed by the object before the task gets added to
	 *  the execution queue.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 */
	public void start(ISpaceObject obj);
	
	/**
	 *  This method will be executed by the object before the task is removed
	 *  from the execution queue.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 */
	public void shutdown(ISpaceObject obj);

	/**
	 *  Executes the task.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 *  @param clock	The clock service.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, long progress, IClockService clock);

	/**
	 *  Check if a task is finished and should be removed.
	 *  Finished tasks will no longer be executed.
	 *  @return	True, if the task is finished.
	 */
	public boolean isFinished(IEnvironmentSpace space, ISpaceObject obj);
}
