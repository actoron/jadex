package jadex.simulation.analysis.buildingBlocks.generalAnalysis.viewer;

import jadex.base.gui.componentviewer.AbstractServiceViewerPanel;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.IGeneralAnalysisService;
import jadex.simulation.analysis.buildingBlocks.generalAnalysis.impl.view.BasicGeneralAnalysisServiceView;

import javax.swing.JComponent;

public class GeneralAnalysisServiceViewerPanel extends AbstractServiceViewerPanel {

	@Override
	public JComponent getComponent() {
		// No use of spezial comp Class
		BasicGeneralAnalysisServiceView comp = new BasicGeneralAnalysisServiceView((IGeneralAnalysisService) service);
		comp.init();
		return comp;
	}
	
	//TODO: JADEX Bug
	@Override
	public IFuture getProperties()
	{
		return new Future(null);
	}

}
