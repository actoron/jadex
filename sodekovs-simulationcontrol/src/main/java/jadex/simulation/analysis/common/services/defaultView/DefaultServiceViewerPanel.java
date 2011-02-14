package jadex.simulation.analysis.common.services.defaultView;

import jadex.base.gui.componentviewer.AbstractServiceViewerPanel;
import jadex.simulation.analysis.common.services.IAnalysisService;

import javax.swing.JComponent;

public class DefaultServiceViewerPanel extends AbstractServiceViewerPanel {

	@Override
	public JComponent getComponent() {
		DefaultServiceView view = new DefaultServiceView((IAnalysisService) service);
		view.init();
		return view;
	}
}
