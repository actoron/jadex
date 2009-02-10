package jadex.bdi.planlib.simsupport.simcap;

import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.ISimulationEventListener;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;

import java.util.List;
import java.util.Map;


/**
 * Plan for creating new simulation objects on a local simulation engine.
 */
public class LocalCreateObjectPlan extends Plan
{
	/**
	 * Creates a new simulation object on the local simulation engine.
	 */
	public void body()
	{
		String type = (String)getParameter("type").getValue();
		Map properties = (Map)getParameter("properties").getValue();
		List tasks = (List)getParameter("tasks").getValue();
		IVector2 position = (IVector2)getParameter("position").getValue();
		boolean signalDestruction = ((Boolean)getParameter("signal_destruction")
				.getValue()).booleanValue();
		boolean listen = ((Boolean)getParameter("listen").getValue())
				.booleanValue();
		IBeliefbase b = getBeliefbase();
		ISimulationEngine engine = (ISimulationEngine)b.getBelief(
				"local_simulation_engine").getFact();
		ISimulationEventListener listener = null;
		if(listen)
		{
			listener = new LocalSimulationEventListener(getExternalAccess());
		}
		Integer objectId = engine.createSimObject(type, properties, tasks,
				position, signalDestruction, listener);
		getParameter("object_id").setValue(objectId);
	}
}
