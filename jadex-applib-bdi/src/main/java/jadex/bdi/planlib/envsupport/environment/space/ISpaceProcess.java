package jadex.bdi.planlib.envsupport.environment.space;

import jadex.bdi.planlib.envsupport.math.IVector1;
import jadex.bridge.IClock;

/**
 * Space process interface. Use this interface to implement new
 * space processes.
 */
public interface ISpaceProcess
{
	/**
	 * This method will be executed by the object before the process gets added
	 * to the execution queue.
	 * 
	 * @param space the space this process is running in
	 */
	public void start(ISpace space);

	/**
	 * This method will be executed by the object before the process is removed
	 * from the execution queue.
	 * 
	 * @param space the space this process is running in
	 */
	public void shutdown(ISpace space);

	/**
	 * Executes the environment process
	 * 
	 * @param clock the clock
	 * @param deltaT time passed during this step
	 * @param space the space this process is running in
	 */
	public void execute(IClock clock, IVector1 deltaT, ISpace space);

	/**
	 * Returns the ID of the process.
	 * 
	 * @return ID of the process.
	 */
	public Object getId();
}
