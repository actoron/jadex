package jadex.simulation.analysis.common.util.controlComponentJadexPanel;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;

import javax.swing.JComponent;

/**
 * Viewer Panel for the jadex componentviewer
 * @author 5Haubeck
 *
 */
public class ControlComponentViewerPanel extends AbstractComponentViewerPanel {

	@Override
	public JComponent getComponent() {
		ControlComponentView compView = new ControlComponentView(component);
		compView.init();
		return compView;
	}
}
