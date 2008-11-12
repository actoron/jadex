package jadex.bdi.planlib.simsupport.simcap;

import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;

/** Plan for setting the position using a local simulation engine.
 */
public class LocalSetPositionPlan extends Plan
{
	/** Sets the current position from the local simulation engine and
	 *  stores it in the belief base.
	 */
	public void body()
	{
		Integer objectId = (Integer) getParameter("object_id").getValue();
		IBeliefbase b = getBeliefbase();
		ISimulationEngine engine =
			(ISimulationEngine) b.getBelief("local_simulation_engine").getFact();
		IVector2 position = (IVector2) getParameter("position").getValue();
		engine.getSimulationObject(objectId).setPosition(position.copy());
	}
}
