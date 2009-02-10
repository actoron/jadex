package jadex.bdi.planlib.simsupport.simcap;

import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.SimulationEngineContainer;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;


public class ConnectEnvironmentPlan extends Plan
{
	public void body()
	{
		String environmentName = (String)getParameter("environment_name")
				.getValue();
		ISimulationEngine engine = SimulationEngineContainer.getInstance()
				.getSimulationEngine(environmentName);

		if(engine == null)
		{
			fail();
		}

		// TODO: Add remote case
		IBeliefbase b = getBeliefbase();
		b.getBelief("local_simulation_engine").setFact(engine);

		b.getBelief("connected").setFact(new Boolean(true));
	}
}
