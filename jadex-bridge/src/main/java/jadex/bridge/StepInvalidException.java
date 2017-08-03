package jadex.bridge;

/**
 *  Exception thrown when a step is invalid on execution.
 */
public class StepInvalidException extends RuntimeException
{
	/** The step. */
	protected IComponentStep<?> step;
	
	/**
	 *  Create a new exception.
	 */
	public StepInvalidException(IComponentStep<?> step)
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
	
	public void printStackTrace()
	{
		super.printStackTrace();
	}
}
