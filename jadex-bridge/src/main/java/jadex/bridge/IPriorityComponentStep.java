package jadex.bridge;

/**
 *  A priority component step can define an execution priority.
 */
public interface IPriorityComponentStep<T> extends IComponentStep<T>
{
	/**
	 *  Get the priority of the step.
	 *  @return The priority x<0 being immediate steps,
	 *  i.e. all steps with prio x<=0 are always executed (even when suspended).
	 *  Default steps get prio 0 (not immediate). 
	 */
	public int getPriority();
}
