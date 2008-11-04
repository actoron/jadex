package jadex.bdi.planlib.simsupport.simcap;

import java.util.List;

import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;

/** Plan for creating new simulation objects on a local simulation engine.
 */
public class LocalCreateObjectPlan extends Plan
{
	/** Creates a new simulation object on the local simulation engine.
	 */
	public void body()
	{
		String type = (String) getParameter("type").getValue();
		IVector2 position = (IVector2) getParameter("position").getValue();
		IVector2 velocity = (IVector2) getParameter("velocity").getValue();
		IDrawable drawable = (IDrawable) getParameter("drawable").getValue();
		IBeliefbase b = getBeliefbase();
		ISimulationEngine engine =
			(ISimulationEngine) b.getBelief("local_simulation_engine").getFact();
		Integer objectId = engine.createSimObject(type, position, velocity, drawable);
		engine.getSimulationObject(objectId).addListener(new LocalSimObjectStateListener(getExternalAccess()));
		getParameter("object_id").setValue(objectId);
	}
}
