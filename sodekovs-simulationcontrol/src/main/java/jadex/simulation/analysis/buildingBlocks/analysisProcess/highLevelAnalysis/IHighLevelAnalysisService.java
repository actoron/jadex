package jadex.simulation.analysis.buildingBlocks.analysisProcess.highLevelAnalysis;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.services.IAnalysisService;

import java.util.UUID;

public interface IHighLevelAnalysisService extends IAnalysisService
{
	public IFuture executeAnalysis(UUID sessionID);
	
	//TODO: public IFuture getRequiredServices();
}
