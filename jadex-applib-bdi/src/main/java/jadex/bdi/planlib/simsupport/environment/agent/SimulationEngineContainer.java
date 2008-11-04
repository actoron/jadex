package jadex.bdi.planlib.simsupport.environment.agent;

import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SimulationEngineContainer
{
	/** The singleton instance
	 */
	private static SimulationEngineContainer instance_;
	
	/** The simulation engines.
	 */
	private Map simulationEngines_;
	
	public static synchronized SimulationEngineContainer getInstance()
	{
		if (instance_ == null)
		{
			instance_ = new SimulationEngineContainer();
		}
		
		return instance_;
	}
	
	private SimulationEngineContainer()
	{
		simulationEngines_ = Collections.synchronizedMap(new HashMap());
	}
	
	/** Adds a new ISimulationEngine.
	 * 
	 * @param name name of the engine
	 * @param engine the engine
	 */
	public void addSimulationEngine(String name, 
									ISimulationEngine engine)
	{
		simulationEngines_.put(name, engine);
	}
	
	/** Removes an ISimulationEngine.
	 * 
	 * @param name name of the engine
	 */
	public void removeSimulationEngine(String name)
	{
		simulationEngines_.remove(name);
	}
	
	/** Returns a local simulation engine.
	 * 
	 *  @param name environment name
	 *  @return local simulation engine or null if it isn't found
	 */
	public ISimulationEngine getSimulationEngine(String name)
	{
		return (ISimulationEngine) simulationEngines_.get(name);
	}
}
