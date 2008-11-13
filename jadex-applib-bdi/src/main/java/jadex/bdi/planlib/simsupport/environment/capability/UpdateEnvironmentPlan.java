package jadex.bdi.planlib.simsupport.environment.capability;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.planlib.simsupport.common.math.Vector1Long;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IClockService;

public class UpdateEnvironmentPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		ISimulationEngine engine =
			(ISimulationEngine) b.getBelief("simulation_engine").getFact();
		long lastTime = ((Long) b.getBelief("sim_time").getFact()).longValue();
		IVector1 timeCoeff = (IVector1) b.getBelief("time_coefficient").getFact();
		//long currentTime = ((Long) b.getBelief("time").getFact()).longValue();
		// TODO: dynamic evaluation not working?
		long currentTime = ((IClockService) b.getBelief("clock_service").getFact()).getTime();
		//System.out.println("SimTime: " + lastTime);
		//System.out.println("Time: " + currentTime);
		IVector1 deltaT = timeCoeff.copy().multiply(new Vector1Long(currentTime - lastTime));
		engine.simulateStep(deltaT);
		b.getBelief("sim_time").setFact(new Long(currentTime));
	}
}
