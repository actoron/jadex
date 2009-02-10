package jadex.bdi.planlib.simsupport.simcap;

import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;

import java.util.List;


/**
 * Plan for retrieving the current position from a local simulation engine.
 */
public class LocalPerformActionPlan extends Plan
{
	/**
	 * Retrieves the current position from the local simulation engine and
	 * stores it in the belief base.
	 */
	public void body()
	{
		String action = (String)getParameter("action").getValue();
		Integer actorId = (Integer)getParameter("actor_id").getValue();
		Integer objectId = (Integer)getParameter("object_id").getValue();
		List parameters = (List)getParameter("parameters").getValue();
		IBeliefbase b = getBeliefbase();
		ISimulationEngine engine = (ISimulationEngine)b.getBelief(
				"local_simulation_engine").getFact();
		boolean result = engine.performAction(action, actorId, objectId,
				parameters);
		if(!result)
		{
			fail();
		}
	}
}
