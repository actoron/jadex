package jadex.simulation.analysis.service.basic.view;

import jadex.base.gui.componentviewer.AbstractServiceViewerPanel;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;

import javax.swing.JComponent;

public class DefaultServiceViewerPanel extends AbstractServiceViewerPanel {

	@Override
	public JComponent getComponent() {
		DefaultServiceView view = new DefaultServiceView((IAnalysisService) service);
		view.init();
		return view;
	}
}
