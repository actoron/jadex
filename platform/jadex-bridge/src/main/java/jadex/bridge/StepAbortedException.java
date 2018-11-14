package jadex.bridge;

/**
 *  Exception thrown when a step is aborted due to end state.
 */
public class StepAbortedException extends RuntimeException
{
	/** The step. */
	protected IComponentStep<?> step;
	
	/**
	 *  Create a new exception.
	 */
	public StepAbortedException(IComponentStep<?> step)
	{
		this.step = step;
	}

	/**
	 *  Get the step.
	 *  @return The step
	 */
	public IComponentStep<?> getStep()
	{
		return step;
	}
	
	/**
	 * Include step in string.
	 */
	@Override
	public String toString()
	{
		return super.toString()+", "+step;
	}
}
