package jadex.bdi.planlib.simsupport.observer.capability;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jadex.bdi.planlib.simsupport.common.graphics.IViewport;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJ2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJOGL;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.DrawableCombiner;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.IDrawable;
import jadex.bdi.planlib.simsupport.common.graphics.drawable.TexturedRectangle;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.IExternalEngineAccess;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;
import jadex.bdi.planlib.simsupport.environment.SimulationEngineContainer;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.ILibraryService;

import javax.media.opengl.GLException;
import javax.swing.JFrame;

public class StartObserverPlan extends Plan
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
		b.getBelief("simulation_engine_access").setFact(engine);
		IVector2 areaSize = engine.getAreaSize();
		
		//TODO: Remote case
		
		boolean forceJ2D = ((Boolean) b.getBelief("force_java2d").getFact()).booleanValue();
		boolean preserveAR = ((Boolean) b.getBelief("preserve_aspect_ratio").getFact()).booleanValue();
		
		ILibraryService libService = (ILibraryService) getScope().getPlatform().getService(ILibraryService.class,
																						   ILibraryService.LIBRARY_SERVICE);
		
		if (libService == null)
		{
			System.err.println("Observer: Library service not found. Exiting.");
			killAgent();
		}
		
		JFrame frame = new JFrame(envName);
		frame.setLayout(new BorderLayout());
		frame.setUndecorated(true);
		frame.pack();
		frame.setSize(1, 1);
		
		boolean useOpenGl = false;
		if (!forceJ2D)
		{
			// Try OpenGL...
			try
			{
				ViewportJOGL vp = new ViewportJOGL(libService);
				frame.add(vp.getCanvas());
				frame.setVisible(true);
				if (!((ViewportJOGL) vp).isValid())
				{
					System.err.println("OpenGL support insufficient, using Java2D fallback...");
				}
				else
				{
					useOpenGl = true;
				}
			}
			catch (GLException e1)
			{
				System.out.println(e1);
				e1.printStackTrace();
				System.err.println("OpenGL initialization failed, using Java2D fallback...");
			}
			catch (Error e2)
			{
				System.out.println(e2);
				e2.printStackTrace();
				System.err.println("OpenGL initialization failed, using Java2D fallback...");
			}
		}
		frame.dispose();
		frame = null;
		
		IViewport viewport = null;
		if (useOpenGl)
		{
			viewport = new ViewportJOGL(libService);
		}
		else
		{
			viewport = new ViewportJ2D(libService);
		}
		
		viewport.setPreserveAspectRation(preserveAR);
		viewport.setSize(areaSize);
		
		// Register the drawables
		Map themes = (Map) b.getBelief("object_themes").getFact();
		synchronized(themes)
		{
			for (Iterator it = themes.values().iterator(); it.hasNext(); )
			{
				Map theme = (Map) it.next();
				Collection drawables = theme.values();
				for (Iterator it2 = drawables.iterator(); it2.hasNext(); )
				{
					DrawableCombiner d = (DrawableCombiner) it2.next();
					viewport.registerDrawableCombiner(d);
				}
			}
		}
		
		if (b.getBelief("selected_theme").getFact() == null)
		{
			b.getBelief("selected_theme").setFact(themes.keySet().toArray()[0]);
		}
		
		b.getBelief("viewport").setFact(viewport);
		
		DrawableCombiner objectMarker = new DrawableCombiner();
		IDrawable markerDrawable = new TexturedRectangle(getClass().getPackage().getName().replaceAll("capability", "").concat("images.").replaceAll("\\.", "/").concat("selection_marker.png"));
		objectMarker.addDrawable(markerDrawable, Integer.MAX_VALUE);
		viewport.registerDrawableCombiner(objectMarker);
		b.getBelief("object_marker").setFact(objectMarker);
		
		boolean customGui = ((Boolean) b.getBelief("custom_gui").getFact()).booleanValue();
		if (customGui)
		{
			b.getBelief("canvas").setFact(viewport.getCanvas());
		}
		else
		{
			new ObserverCenter(getExternalAccess());
		}
		
		IGoal updateGoal = createGoal("simobs_update_display");
		dispatchTopLevelGoal(updateGoal);
	}
}
