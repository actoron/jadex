package jadex.extension.envsupport.environment;

import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.IBooleanCondition;
import jadex.commons.SimplePropertyObject;

/**
 *  Empty default implementation for object tasks.
 */
public abstract class AbstractTask extends SimplePropertyObject implements IObjectTask
{
	//-------- constants --------
	
	/** The task condition property. */
	public static final String	PROPERTY_CONDITION	= "condition";
	
	//-------- attributes --------
	
	/** The finished flag. */
	protected boolean	finished;
	
	/** The task condition (optional). Task is executed as long as the condition is true. */
	protected IBooleanCondition	condition;
	
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
		this.condition	= (IBooleanCondition)getProperty(PROPERTY_CONDITION);
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

//	/**
//	 *  Executes the task.
//	 *  Handles exceptions. Subclasses should implement doExecute() instead.
//	 *  @param space	The environment in which the task is executing.
//	 *  @param obj	The object that is executing the task.
//	 *  @param progress	The time that has passed according to the environment executor.
//	 */
//	public void execute(IEnvironmentSpace space, ISpaceObject obj, IVector1 progress)
//	{
//	}
	
	/**
	 *  Executes the task.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 *  @param clock	The clock service.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, long progress, IClockService clock)
	{
	}


	/**
	 *  Check if a task is finished and should be removed.
	 *  Finished tasks will no longer be executed.
	 *  @return	True, if the task is finished.
	 */
	public boolean isFinished(IEnvironmentSpace space, ISpaceObject obj)
	{
		return finished || condition!=null && !condition.isValid();
	}

	//-------- helper methods --------
	
	/**
	 *  Indicate that the task is finished and should be removed.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param finished	The finished flag.
	 */
	public void	setFinished(IEnvironmentSpace space, ISpaceObject obj, boolean finished)
	{
		this.finished	= finished;
		
		// remove task immediately (otherwise will only be removed in next step).
		if(finished)
			space.removeObjectTask(this.getProperty(PROPERTY_ID), obj.getId());
	}
}
