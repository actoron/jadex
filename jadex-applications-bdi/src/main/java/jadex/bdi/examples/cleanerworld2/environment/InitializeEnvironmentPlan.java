package jadex.bdi.examples.cleanerworld2.environment;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.examples.cleanerworld2.environment.action.PickupWasteAction;
import jadex.bdi.examples.cleanerworld2.environment.process.WasteGenerationProcess;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.ScalableTexturedRectangle;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.graphics.layer.TiledLayer;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
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
		
		ILayer background =  new TiledLayer(Configuration.BACKGROUND_TILE_SIZE,
											Configuration.BACKGROUND_TILE);
		
		engine.addPreLayer(background);
		
		// Static Objects
		String imgPath = this.getClass().getPackage().getName().replaceAll("environment", "").concat("images.").replaceAll("\\.", "/");
		
		int wasteBinCount = ((Integer) b.getBelief("waste_bin_count").getFact()).intValue();
		for (int i = 0; i < wasteBinCount; ++i)
		{
			IVector2 pos = engine.getRandomPosition(Configuration.WASTE_BIN_SIZE);
			IDrawable wbDrawable = new ScalableTexturedRectangle(Configuration.WASTE_BIN_SIZE, imgPath + "wastebin.png");
			engine.createSimObject("waste_bin", null, null, pos, wbDrawable, false, null);
			
		}
		int chargingStationCount = ((Integer) b.getBelief("charging_station_count").getFact()).intValue();
		for (int i = 0; i < chargingStationCount; ++i)
		{
			IVector2 pos = engine.getRandomPosition(Configuration.CHARGING_STATION_SIZE);
			IDrawable csDrawable = new ScalableTexturedRectangle(Configuration.CHARGING_STATION_SIZE, imgPath + "chargingstation.png");
			engine.createSimObject("charging_station", null, null, pos, csDrawable, false, null);
		}
		
		// Processes
		engine.addEnvironmentProcess(new WasteGenerationProcess());
		
		// Actions
		engine.addAction(new PickupWasteAction());
		
		b.getBelief("simulation_engine").setFact(engine);
		
		
		IGoal start = createGoal("sim_start_environment");
		dispatchSubgoalAndWait(start);
	}
	
}
