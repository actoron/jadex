package jadex.simulation.analysis.application.opt4j;

import org.opt4j.core.IndividualStateListener;
import org.opt4j.viewer.ControlButtons;
import org.opt4j.viewer.ControlToolBarService;
import org.opt4j.viewer.Progress;
import org.opt4j.viewer.StatusBar;
import org.opt4j.viewer.ViewerModule;
import org.opt4j.viewer.Viewport;
import org.opt4j.viewer.ViewsToolBarService;

public class ViewerSimModule extends ViewerModule
{
	@Override
	public void config()
	{
		bind(ViewerSim.class).in(SINGLETON);
		addOptimizerStateListener(ViewerSim.class);

		bind(StatusBar.class).in(SINGLETON);
		addOptimizerStateListener(StatusBar.class);
		addOptimizerIterationListener(StatusBar.class);
		
		bind(Viewport.class).in(SINGLETON);
		
		addOptimizerIterationListener(Progress.class);
		addOptimizerStateListener(ControlButtons.class);
		
		addOptimizerIterationListener(ConvergencePlotDataSim.class);
		
		addToolBarService(ControlToolBarService.class);
		addToolBarService(ViewsToolBarService.class);
	}

}
