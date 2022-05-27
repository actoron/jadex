package jadex.bridge;

import jadex.bridge.component.IExecutionFeature;

/**
 *  A priority component step can define an execution priority.
 */
public interface IPriorityComponentStep<T> extends IComponentStep<T>
{
	/**
	 *  Get the priority of the step.
	 *  @return The priority. Some priority levels are defined
	 *  as constants in IExecutionFeature.
	 */
	//public int getPriority();
	
	/**
	 *  Get the priority of the step.
	 *  @return The priority. Some priority levels are defined
	 *  as constants in IExecutionFeature.
	 */
	public default int getPriority()
	{
		return IExecutionFeature.STEP_PRIORITY_IMMEDIATE;
	}
	
	/**
	 *  Get the inherit flag.
	 *  @return True, if priority should be inherited.
	 */
	public default boolean isInherit()
	{
		return false;
	}
}
