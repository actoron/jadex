package jadex.adapter.base.envsupport.environment;

/**
 * Interface for a space executor that executes steps in a space.
 */
public interface ISpaceExecutor
{
	/**
	 * Sets the space for the executor. Called by the space when the executor is added.
	 * @param space the space being executed
	 */
	public void setSpace(IEnvironmentSpace space);
}
