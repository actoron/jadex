package jadex.simulation.analysis.service.simulation.execution;

import java.util.UUID;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.process.basicTasks.IATaskView;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;
import jadex.simulation.analysis.service.simulation.ISimulationService;

/**
 *  The simulation execution interface for executing experiments.
 */
public interface IAExperimentAusfuehrenService extends ISimulationService
{

	/**
	 *  Execute a IAExperiment
	 *  @param experiment {@link IAExperiment}
	 * @param view 
	 */
	public IFuture executeExperiment(UUID session, IAExperiment experiment);
}
