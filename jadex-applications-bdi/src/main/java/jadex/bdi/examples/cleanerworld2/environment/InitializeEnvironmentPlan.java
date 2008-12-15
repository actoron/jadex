package jadex.bdi.examples.cleanerworld2.environment;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.examples.cleanerworld2.environment.action.ChargeBatteryAction;
import jadex.bdi.examples.cleanerworld2.environment.action.DisposeWasteAction;
import jadex.bdi.examples.cleanerworld2.environment.action.PickupWasteAction;
import jadex.bdi.examples.cleanerworld2.environment.process.WasteGenerationProcess;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.TexturedRectangle;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.graphics.layer.TiledLayer;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.planlib.simsupport.environment.EuclideanSimulationEngine;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

public class InitializeEnvironmentPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		b.getBelief("environment_name").setFact(Configuration.ENVIRONMENT_NAME);
		b.getBelief("clock_service").setFact(getClock());
		
		ISimulationEngine engine  = new EuclideanSimulationEngine(Configuration.ENVIRONMENT_NAME,
																  Configuration.AREA_SIZE);
		
		// Pre-declare object types
		engine.declareObjectType("waste");
		engine.declareObjectType("waste_bin");
		engine.declareObjectType("charging_station");
		engine.declareObjectType("cleaner");
		
		// Static Objects
		int wasteBinCount = ((Integer) b.getBelief("waste_bin_count").getFact()).intValue();
		for (int i = 0; i < wasteBinCount; ++i)
		{
			IVector2 pos = engine.getRandomPosition(Configuration.WASTE_BIN_SIZE);
			engine.createSimObject("waste_bin", null, null, pos, false, null);
			
		}
		int chargingStationCount = ((Integer) b.getBelief("charging_station_count").getFact()).intValue();
		for (int i = 0; i < chargingStationCount; ++i)
		{
			IVector2 pos = engine.getRandomPosition(Configuration.CHARGING_STATION_SIZE);
			engine.createSimObject("charging_station", null, null, pos, false, null);
		}
		
		// Processes
		int maxWastes = ((Integer) getBeliefbase().getBelief("max_wastes").getFact()).intValue();
		if (maxWastes <= 0)
		{
			maxWastes = 1;
		}
		IVector1 wasteSpawnRate = (IVector1) getBeliefbase().getBelief("waste_spawn_rate").getFact();
		if ((wasteSpawnRate.less(Vector1Double.ZERO)) ||
			(wasteSpawnRate.equals(Vector1Double.ZERO)))
		{
			wasteSpawnRate = new Vector1Double(1.0);
		}
		engine.addEnvironmentProcess(new WasteGenerationProcess(maxWastes, wasteSpawnRate));
		
		// Actions
		engine.addAction(new PickupWasteAction());
		engine.addAction(new DisposeWasteAction());
		engine.addAction(new ChargeBatteryAction());
		
		b.getBelief("simulation_engine").setFact(engine);
		
		
		IGoal start = createGoal("sim_start_environment");
		dispatchSubgoalAndWait(start);
	}
	
}
