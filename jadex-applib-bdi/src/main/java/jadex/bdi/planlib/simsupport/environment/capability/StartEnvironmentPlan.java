package jadex.bdi.planlib.simsupport.environment.capability;

import java.util.Iterator;
import java.util.List;

import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.EuclideanSimulationEngine;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.SimulationEngineContainer;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IClockService;

public class StartEnvironmentPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		String name = ((String) b.getBelief("environment_name").getFact());
		
		IClockService clockService = (IClockService) b.getBelief("clock_service").getFact();
		if (clockService == null)
		{
			clockService = getClock();
			b.getBelief("clock").setFact(clockService);
		}
		b.getBelief("sim_time").setFact(new Long(clockService.getTime()));
		
		ISimulationEngine engine = (ISimulationEngine) b.getBelief("simulation_engine").getFact();
		
		SimulationEngineContainer.getInstance().addSimulationEngine(name, engine);
		
		IGoal updateGoal = createGoal("update_environment");
		dispatchTopLevelGoal(updateGoal);
	}
}
