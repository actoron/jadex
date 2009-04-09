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
	 *  @param progress some indicator of progress (may be time, step number or set to 0 if not needed)
	 *  @param space the space this process is running in
	 */
	public void execute(IVector1 progress, IEnvironmentSpace space);
}
