package jadex.bdi.planlib.simsupport.simcap;

import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.process.IEnvironmentProcess;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;


/**
 * Plan for adding a new environment process.
 */
public class LocalAddEnvironmentProcessPlan extends Plan
{
	/**
	 * Sets the new velocity of the object.
	 */
	public void body()
	{
		IEnvironmentProcess process = (IEnvironmentProcess)getParameter(
				"process").getValue();
		IBeliefbase b = getBeliefbase();
		ISimulationEngine engine = (ISimulationEngine)b.getBelief(
				"local_simulation_engine").getFact();
		engine.addEnvironmentProcess(process);
	}
}
