package jadex.bridge;

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
	public int getPriority();
}
