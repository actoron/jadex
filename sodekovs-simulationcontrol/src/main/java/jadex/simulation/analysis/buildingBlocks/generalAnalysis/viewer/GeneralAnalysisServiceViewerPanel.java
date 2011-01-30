package jadex.simulation.analysis.buildingBlocks.generalAnalysis.viewer;

import jadex.base.gui.componentviewer.AbstractServiceViewerPanel;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.IGeneralAnalysisService;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.workflowImpl.view.BasicGeneralAnalysisServiceView;

import javax.swing.JComponent;

public class GeneralAnalysisServiceViewerPanel extends AbstractServiceViewerPanel {

	@Override
	public JComponent getComponent() {
		// No use of spezial comp Class
		BasicGeneralAnalysisServiceView comp = new BasicGeneralAnalysisServiceView();
		comp.init((IGeneralAnalysisService) service);
		return comp;
	}

}
