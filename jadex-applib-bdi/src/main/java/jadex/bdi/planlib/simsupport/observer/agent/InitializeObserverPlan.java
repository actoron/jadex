package jadex.bdi.planlib.simsupport.observer.agent;

import jadex.bdi.planlib.simsupport.common.graphics.IViewport;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJ2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJOGL;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.SimulationEngineContainer;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.ILibraryService;

public class InitializeObserverPlan extends Plan
{
	public void body()
	{
		IBeliefbase b = getBeliefbase();
		String envName = (String) b.getBelief("environment_name").getFact();
		
		// Attempt to connect locally
		ISimulationEngine engine = null;
		
		while (engine == null)
		{
			engine =
				SimulationEngineContainer.getInstance().getSimulationEngine(envName);
			waitFor(100);
		}
		
		b.getBelief("local_simulation_engine").setFact(engine);
		IVector2 areaSize = engine.getAreaSize();
		
		//TODO: Remote case
		
		boolean forceJ2D = ((Boolean) b.getBelief("force_java2d").getFact()).booleanValue();
		boolean preserveAR = ((Boolean) b.getBelief("preserve_aspect_ratio").getFact()).booleanValue();
		
		ILibraryService libService = (ILibraryService) getScope().getPlatform().getService(ILibraryService.class,
																						   ILibraryService.LIBRARY_SERVICE);
		System.out.println(libService);
		if (libService == null)
		{
			System.exit(1);
		}
		
		IViewport viewport = null;
		
		if (!forceJ2D)
		{
			// Try OpenGL first...
			try
			{
				ViewportJOGL vp = new ViewportJOGL(envName, 0.0, libService);
				if (vp.isValid())
				{
					viewport = vp;
				}
			}
			catch (Error e)
			{
				e.printStackTrace();
				viewport = null;
			}
		}
		
		if (viewport == null)
		{
			// Use Java2D
			viewport = new ViewportJ2D(envName, 0.0, libService);
		}
		
		viewport.setPreserveAspectRation(preserveAR);
		
		viewport.setSize(areaSize);
		
		b.getBelief("viewport").setFact(viewport);
		
		IGoal updateGoal = createGoal("simobs_update_display");
		dispatchTopLevelGoal(updateGoal);
	}
}
