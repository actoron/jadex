package jadex.simulation.analysis.service.simulation.allocation;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.UUID;

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
