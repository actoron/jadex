package jadex.simulation.analysis.common.component.workflow.defaultView;

import jadex.base.gui.componentviewer.AbstractComponentViewerPanel;

import javax.swing.JComponent;

public class BpmnComponentViewerPanel extends AbstractComponentViewerPanel {

	@Override
	public JComponent getComponent() {
		BpmnComponentView comp = new BpmnComponentView(component);
		return comp;
	}
	

}
