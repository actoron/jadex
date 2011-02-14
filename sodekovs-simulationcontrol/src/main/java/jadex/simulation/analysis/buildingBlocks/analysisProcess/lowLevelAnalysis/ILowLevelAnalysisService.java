package jadex.simulation.analysis.buildingBlocks.analysisProcess.lowLevelAnalysis;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.services.IAnalysisService;

import java.util.UUID;

public interface ILowLevelAnalysisService extends IAnalysisService
{
	public IFuture executeAnalysis(UUID sessionID); //TODO: give Process
	
	public IFuture getTasks(UUID sessionID);
	
	
	
	//TODO: public IFuture getRequiredServices();
}
