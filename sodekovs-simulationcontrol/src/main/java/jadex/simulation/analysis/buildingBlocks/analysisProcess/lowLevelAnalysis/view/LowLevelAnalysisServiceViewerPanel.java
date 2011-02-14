package jadex.simulation.analysis.buildingBlocks.analysisProcess.lowLevelAnalysis.view;

import jadex.base.gui.componentviewer.AbstractServiceViewerPanel;
import jadex.simulation.analysis.buildingBlocks.analysisProcess.lowLevelAnalysis.ILowLevelAnalysisService;
import jadex.simulation.analysis.buildingBlocks.analysisProcess.lowLevelAnalysis.impl.view.LowLevelAnalysisServiceView;

import javax.swing.JComponent;

public class LowLevelAnalysisServiceViewerPanel extends AbstractServiceViewerPanel {

	@Override
	public JComponent getComponent() {
		LowLevelAnalysisServiceView comp = new LowLevelAnalysisServiceView((ILowLevelAnalysisService) service);
		comp.init();
		return comp;
	}
}
