package jadex.simulation.analysis.buildingBlocks.analysisProcess.lowLevelAnalysis.view;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.bridge.service.IService;
import jadex.simulation.analysis.buildingBlocks.analysisProcess.lowLevelAnalysis.ILowLevelAnalysisService;
import jadex.simulation.analysis.common.services.defaultView.DefaultServicePlugin;

/**
 *  Used to show the low level analysis service view
 */
public class LowLevelAnalysisServicePlugin extends DefaultServicePlugin
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
	
//	@Override
//	/**
//	 *  Create the service panel.
//	 */
//	public IFuture createServicePanel(IService service)
//	{
//		LowLevelAnalysisServiceViewerPanel view= new LowLevelAnalysisServiceViewerPanel();
//		view.init(getJCC(), service);
//		return new Future(null);
//	}
}
