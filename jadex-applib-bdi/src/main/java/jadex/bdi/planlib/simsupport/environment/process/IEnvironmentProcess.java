package jadex.bdi.planlib.simsupport.environment.process;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;

/** Environment process interface. Use this interface to implement
 *  new environment processes.
 */
public interface IEnvironmentProcess
{
	/** Executes the environment process
	 *  
	 *  @param deltaT time passed during this simulation step
	 *  @param engine the simulation engine
	 */
	public void execute(IVector1 deltaT, ISimulationEngine engine);
}
