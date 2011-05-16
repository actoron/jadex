package jadex.simulation.analysis.common.component.microAgent.defaultView;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;

import javax.swing.JComponent;

public class MicroComponentViewerPanel extends AbstractComponentViewerPanel {

	@Override
	public JComponent getComponent() {
		MicroComponentView compView = new MicroComponentView(component);
		compView.init();
		return compView;
	}
}
