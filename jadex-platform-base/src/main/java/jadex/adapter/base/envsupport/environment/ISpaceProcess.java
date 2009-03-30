package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;

/**
 * Space process interface. Use this interface to implement new
 * space processes.
 */
public interface ISpaceProcess
{
	/**
	 *  This method will be executed by the object before the process gets added
	 *  to the execution queue.
	 *  @param space the space this process is running in
	 */
	public void start(IEnvironmentSpace space);

	/**
	 *  This method will be executed by the object before the process is removed
	 *  from the execution queue.
	 *  @param space the space this process is running in
	 */
	public void shutdown(IEnvironmentSpace space);

	/**
	 *  Executes the environment process
	 *  @param time the current time
	 *  @param deltaT time passed during this step
	 *  @param space the space this process is running in
	 */
	public void execute(long time, IVector1 deltaT, IEnvironmentSpace space);

	/**
	 *  Returns the id of the process.
	 *  @return id of the process.
	 */
	public Object getId();
}
