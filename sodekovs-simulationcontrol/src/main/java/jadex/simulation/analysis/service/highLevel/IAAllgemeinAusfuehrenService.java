package jadex.simulation.analysis.service.highLevel;

import java.util.UUID;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;

public interface IAAllgemeinAusfuehrenService extends IAnalysisSessionService
{
	public IFuture ausführen(UUID session, IAExperimentBatch experiment);
}
