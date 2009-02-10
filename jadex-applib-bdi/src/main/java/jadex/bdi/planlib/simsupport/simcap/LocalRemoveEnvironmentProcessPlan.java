package jadex.bdi.planlib.simsupport.simcap;

import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;


/**
 * Plan for adding a new environment process.
 */
public class LocalRemoveEnvironmentProcessPlan extends Plan
{
	/**
	 * Sets the new velocity of the object.
	 */
	public void body()
	{
		String process = (String)getParameter("process_name").getValue();
		IBeliefbase b = getBeliefbase();
		ISimulationEngine engine = (ISimulationEngine)b.getBelief(
				"local_simulation_engine").getFact();
		engine.removeEnvironmentProcess(process);
	}
}
