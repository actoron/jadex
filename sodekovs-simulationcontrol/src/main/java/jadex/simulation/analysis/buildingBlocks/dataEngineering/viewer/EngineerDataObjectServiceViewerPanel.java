package jadex.simulation.analysis.buildingBlocks.dataEngineering.viewer;

import jadex.base.gui.componentviewer.AbstractServiceViewerPanel;
import jadex.simulation.analysis.buildingBlocks.dataEngineering.IEngineerDataObjectService;
import jadex.simulation.analysis.buildingBlocks.dataEngineering.impl.view.EngineerDataObjectServiceView;

import javax.swing.JComponent;

public class EngineerDataObjectServiceViewerPanel extends AbstractServiceViewerPanel {

	@Override
	public JComponent getComponent() {
		EngineerDataObjectServiceView comp = new EngineerDataObjectServiceView((IEngineerDataObjectService) service);
		comp.init();
		return comp;
	}
}
