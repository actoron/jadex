package jadex.simulation.analysis.buildingBlocks.analysisProcess.highLevelAnalysis.view;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.service.IService;
import jadex.simulation.analysis.buildingBlocks.analysisProcess.lowLevelAnalysis.ILowLevelAnalysisService;
import jadex.simulation.analysis.common.services.defaultView.DefaultServicePlugin;

/**
 *  Used to show the low level analysis service view
 */
public class HighLevelAnalysisServicePlugin extends DefaultServicePlugin
{
	
	@Override
	/**
	 *  Get the service type.
	 *  @return The service type.
	 */
	public Class getServiceType()
	{
		return ILowLevelAnalysisService.class;
	}
	
	@Override
	/**
	 *  Create the service panel.
	 */
	public IFuture createServicePanel(IService service)
	{
		HighLevelAnalysisServiceViewerPanel view= new HighLevelAnalysisServiceViewerPanel();
		view.init(getJCC(), service);
		return new Future(null);
	}
}
