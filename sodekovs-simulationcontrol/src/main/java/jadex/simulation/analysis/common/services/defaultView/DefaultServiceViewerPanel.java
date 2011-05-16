package jadex.simulation.analysis.common.services.defaultView;

import jadex.base.gui.componentviewer.AbstractServiceViewerPanel;
import jadex.simulation.analysis.common.services.IAnalysisSessionService;

import javax.swing.JComponent;

public class DefaultServiceViewerPanel extends AbstractServiceViewerPanel {

	@Override
	public JComponent getComponent() {
		DefaultServiceView view = new DefaultServiceView((IAnalysisSessionService) service);
		view.init();
		return view;
	}
}
