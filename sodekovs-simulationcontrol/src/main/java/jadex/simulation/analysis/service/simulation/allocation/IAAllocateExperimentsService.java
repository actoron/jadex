package jadex.simulation.analysis.service.simulation.allocation;

import java.util.UUID;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;

/**
 *  A Service to allocate an experiment
 */
public interface IAAllocateExperimentsService extends IAnalysisSessionService
{

	/**
	 *  Allocate a experimentBatch with default strategy
	 *  @param experiment the experiment
	 *  @return List<IAExecuteExperimentsService>
	 */
	public IFuture allocateExperiment(UUID sessionId, IAExperimentBatch experiments);
}
