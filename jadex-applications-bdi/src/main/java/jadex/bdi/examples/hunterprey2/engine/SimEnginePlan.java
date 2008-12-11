package jadex.bdi.examples.hunterprey2.engine;

import jadex.bdi.examples.hunterprey2.Configuration;
import jadex.bdi.planlib.simsupport.environment.EuclideanSimulationEngine;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class SimEnginePlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		
		b.getBelief("environment_name").setFact(Configuration.ENVIRONMENT_NAME);
		b.getBelief("clock_service").setFact(getClock());
		
		ISimulationEngine engine  = new EuclideanSimulationEngine(
											Configuration.ENVIRONMENT_NAME,
											Configuration.AREA_SIZE);
		
		b.getBelief("simulation_engine").setFact(engine);
		
		IGoal start = createGoal("sim_start_environment");
		dispatchSubgoalAndWait(start);
		
	}
	
}
