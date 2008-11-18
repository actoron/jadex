package jadex.bdi.examples.cleanerworld2.environment;

import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.examples.cleanerworld2.environment.process.WasteGenerationProcess;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.graphics.layer.TiledLayer;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
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
		engine.addEnvironmentProcess(new WasteGenerationProcess());
		
		ILayer background =  new TiledLayer(Configuration.BACKGROUND_TILE_SIZE,
											Configuration.BACKGROUND_TILE);
		
		engine.addPreLayer(background);
		
		IGoal start = createGoal("sim_start_environment");
		dispatchTopLevelGoal(start);
	}
	
}
