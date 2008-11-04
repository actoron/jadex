package jadex.bdi.planlib.simsupport.environment.agent;

import java.util.Iterator;
import java.util.List;

import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.EuclideanSimulationEngine;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IClockService;

public class InitializeEnvironmentPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		int shape = ((Integer) b.getBelief("shape").getFact()).intValue();
		String name = ((String) b.getBelief("environment_name").getFact());
		IVector2 areaSize = ((IVector2) b.getBelief("area_size").getFact());
		
		IClockService clockService = (IClockService) b.getBelief("clock_service").getFact();
		if (clockService == null)
		{
			clockService = getClock();
			b.getBelief("clock").setFact(clockService);
		}
		b.getBelief("sim_time").setFact(new Long(clockService.getTime()));
		ISimulationEngine engine = null;
		switch (shape)
		{
		case ISimulationEngine.EUCLIDEAN_SHAPE:
			engine = new EuclideanSimulationEngine(name, areaSize);
			break;
			
		default:
			System.err.println("Simsupport: Unknown Shape");
			killAgent();
			fail();
		}
		
		b.getBelief("simulation_engine").setFact(engine);
		
		SimulationEngineContainer.getInstance().addSimulationEngine(name, engine);
		
		List bgLayers = (List) b.getBelief("background_layers").getFact();
		for (Iterator it = bgLayers.iterator(); it.hasNext(); )
		{
			ILayer layer = (ILayer) it.next();
			engine.addPreLayer(layer);
		}
		
		IGoal updateGoal = createGoal("update_environment");
		dispatchTopLevelGoal(updateGoal);
	}
}
