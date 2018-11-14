package jadex.bridge;

import jadex.commons.SReflect;

/**
 *  Exception thrown when a step is aborted due to end state.
 */
public class StepAbortedException extends RuntimeException
{
	/** The step. */
	protected IComponentStep<?> step;
	
	/** The step string (for remote). */
	protected String stepstring;
	
	/**
	 *  Create a new exception.
	 */
	public StepAbortedException()
	{
		// Bean constructor
	}

	/**
	 *  Create a new exception.
	 */
	public StepAbortedException(IComponentStep<?> step)
	{
		this.step = step;
		this.stepstring = step!=null? SReflect.getClassName(step.getClass())+" "+step.toString(): null;
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
	 *  Get the step string.
	 *  @return the stepstring
	 */
	public String getStepstring()
	{
		return stepstring;
	}

	/**
	 *  Set the step string.
	 *  @param stepstring the stepstring to set
	 */
	public void setStepstring(String stepstring)
	{
		this.stepstring = stepstring;
	}

	/**
	 * Include step in string.
	 */
	@Override
	public String toString()
	{
		return super.toString()+", "+step + ", " + step.getClass();
	}
}
