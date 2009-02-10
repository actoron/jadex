package jadex.bdi.planlib.simsupport.environment.process;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;


/**
 * Environment process interface. Use this interface to implement new
 * environment processes.
 */
public interface IEnvironmentProcess
{
	/**
	 * This method will be executed by the object before the process gets added
	 * to the execution queue.
	 * 
	 * @param engine the engine that is executing the process
	 */
	public void start(ISimulationEngine engine);

	/**
	 * This method will be executed by the object before the process is removed
	 * from the execution queue.
	 * 
	 * @param object the object that is executing the process
	 */
	public void shutdown(ISimulationEngine engine);

	/**
	 * Executes the environment process
	 * 
	 * @param deltaT time passed during this simulation step
	 * @param engine the simulation engine
	 */
	public void execute(IVector1 deltaT, ISimulationEngine engine);

	/**
	 * Returns the name of the process.
	 * 
	 * @return name of the process.
	 */
	public String getName();
}
