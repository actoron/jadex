package jadex.simulation.analysis.common.defaultViews.controlComponent;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;

import javax.swing.JComponent;

public class ControlComponentViewerPanel extends AbstractComponentViewerPanel {

	@Override
	public JComponent getComponent() {
		ControlComponentView compView = new ControlComponentView(component);
		compView.init();
		return compView;
	}
}
