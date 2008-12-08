package jadex.bdi.examples.cleanerworld2.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jadex.adapter.base.clock.SystemClock;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.examples.cleanerworld2.Configuration;
import jadex.bdi.examples.cleanerworld2.environment.process.WasteGenerationProcess;
import jadex.bdi.planlib.simsupport.common.graphics.layer.ILayer;
import jadex.bdi.planlib.simsupport.common.graphics.layer.TiledLayer;
import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.Vector1Double;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.SimulationEngineContainer;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IAgentIdentifier;
import jadex.commons.concurrent.IResultListener;

public class StartPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		Integer maxWastes = (Integer) b.getBelief("max_wastes").getFact();
		Double wasteSpawnRateDouble = (Double) b.getBelief("waste_spawn_rate").getFact();
		IVector1 wasteSpawnRate = new Vector1Double(wasteSpawnRateDouble.doubleValue());
		
		final IAMS ams =
			(IAMS) getScope().getPlatform().getService(IAMS.class);
		
		Map environmentArgs = new HashMap();
		environmentArgs.put("max_wastes", maxWastes);
		environmentArgs.put("waste_spawn_rate", wasteSpawnRate);
		final String envName = Configuration.ENVIRONMENT_NAME;
		ams.createAgent("CleanerWorld2_Environment",
						"jadex/bdi/examples/cleanerworld2/environment/Environment.agent.xml",
						"default",
						environmentArgs,
						new IResultListener()
							{
								public void resultAvailable(Object result)
								{
									IAgentIdentifier aid = (IAgentIdentifier) result;
									ams.startAgent(aid, null);
								}
								
								public void exceptionOccurred(Exception exception)
								{
									exception.printStackTrace();
								}
							});
		ISimulationEngine engine = null;
		while (engine == null)
		{
			engine = SimulationEngineContainer.getInstance().getSimulationEngine(envName);
			waitFor(100);
		}
		
		int maxCleaner = ((Integer) b.getBelief("cleaner_count").getFact()).intValue();
		if (maxCleaner <= 0)
		{
			maxCleaner = 1;
		}
		
		for (int i = 0; i < maxCleaner; ++i){
			environmentArgs = new HashMap();
//			ams.createAgent("CleanerWorld2_Cleaner" + Integer.valueOf(i).toString(),
			ams.createAgent("CleanerWorld2_Cleaner" + i,	// 1.4 compliant code
					"jadex/bdi/examples/cleanerworld2/cleaner/Cleaner.agent.xml",
					"default",
					environmentArgs,
					new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							IAgentIdentifier aid = (IAgentIdentifier) result;
							ams.startAgent(aid, null);
						}

						public void exceptionOccurred(Exception exception)
						{
							exception.printStackTrace();
						}
					});
			waitFor(100);
		}
		
		environmentArgs = new HashMap();
		ams.createAgent("CleanerWorld2_Observer",
				"jadex/bdi/examples/cleanerworld2/observer/Observer.agent.xml",
				"default",
				environmentArgs,
				new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							IAgentIdentifier aid = (IAgentIdentifier) result;
							ams.startAgent(aid, null);
						}
						
						public void exceptionOccurred(Exception exception)
						{
							exception.printStackTrace();
						}
					});
		
		killAgent();
	}
}
