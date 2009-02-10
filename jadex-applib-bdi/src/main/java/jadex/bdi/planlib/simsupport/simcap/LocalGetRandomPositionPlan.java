package jadex.bdi.planlib.simsupport.simcap;

import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;


/**
 * Plan for retrieving the current position from a local simulation engine.
 */
public class LocalGetRandomPositionPlan extends Plan
{
	/**
	 * Retrieves the current position from the local simulation engine and
	 * stores it in the belief base.
	 */
	public void body()
	{
		IVector2 distance = (IVector2)getParameter("distance").getValue();
		IBeliefbase b = getBeliefbase();
		ISimulationEngine engine = (ISimulationEngine)b.getBelief(
				"local_simulation_engine").getFact();
		IVector2 position = engine.getRandomPosition(distance);
		getParameter("position").setValue(position);
	}
}
