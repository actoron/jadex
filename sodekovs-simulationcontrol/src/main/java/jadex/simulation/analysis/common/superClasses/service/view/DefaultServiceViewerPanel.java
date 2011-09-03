package jadex.simulation.analysis.common.superClasses.service.view;

import jadex.base.gui.componentviewer.AbstractServiceViewerPanel;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisService;

import javax.swing.JComponent;

/**
 * A default Service Panel for Jadex Componentviewer
 * @author 5Haubeck
 *
 */
public class DefaultServiceViewerPanel extends AbstractServiceViewerPanel {

	@Override
	public JComponent getComponent() {
		DefaultServiceView view = new DefaultServiceView((IAnalysisService) service);
		view.init();
		return view;
	}
}
