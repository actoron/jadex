package jadex.simulation.analysis.application.opt4j;

import org.opt4j.core.optimizer.Control;
import org.opt4j.core.optimizer.Optimizer;
import org.opt4j.start.Constant;
import org.opt4j.viewer.StatusBar;
import org.opt4j.viewer.ToolBar;
import org.opt4j.viewer.Viewer;
import org.opt4j.viewer.Viewport;

import com.google.inject.Inject;

public class ViewerSim extends Viewer
{
	private Boolean init = false;
	@Inject
	public ViewerSim(
			Viewport viewport,
			ToolBar toolBar,
			StatusBar statusBar,
			Control control,
			@Constant(value = "title", namespace = Viewer.class) String title,
			@Constant(value = "closeEvent", namespace = Viewer.class) CloseEvent closeEvent,
			@Constant(value = "closeOnStop", namespace = Viewer.class) boolean closeOnStop) {
		super(viewport, toolBar, statusBar, control, title, closeEvent,closeOnStop);
	}
	
	@Override
	public void optimizationStarted(Optimizer arg0)
	{
		if (!init)
		{
			super.optimizationStarted(arg0);
			init = true;
		}		
	}

}
