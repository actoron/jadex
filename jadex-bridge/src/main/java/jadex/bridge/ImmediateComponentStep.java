package jadex.bridge;

import jadex.bridge.component.IExecutionFeature;

/**
 *  Simple abstract class for immediate component steps.
 *  Allows for implementing the execute() logic only.
 */
public abstract class ImmediateComponentStep<T> implements IPriorityComponentStep<T>
{
	/**
	 *  Get the priority of the step.
	 *  @return The priority. Some priority levels are defined
	 *  as constants in IExecutionFeature.
	 */
	public int getPriority()
	{
		return IExecutionFeature.STEP_PRIORITY_IMMEDIATE;
	}
}
