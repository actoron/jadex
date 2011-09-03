package jadex.simulation.analysis.service.highLevel;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.UUID;

public interface IAGeneralExecuteService extends IAnalysisSessionService
{
	public IFuture execute(UUID session, IAExperimentBatch experiment);
}
