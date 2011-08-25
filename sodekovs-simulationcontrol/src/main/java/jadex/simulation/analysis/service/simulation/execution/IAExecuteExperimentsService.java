package jadex.simulation.analysis.service.simulation.execution;

import java.util.Set;
import java.util.UUID;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.process.basicTasks.IATaskView;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;
import jadex.simulation.analysis.service.simulation.Modeltype;

/**
 *  The simulation execution interface for executing experiments.
 */
public interface IAExecuteExperimentsService extends IAnalysisSessionService
{

	/**
	 *  Execute a IAExperiment
	 *  @param experiment {@link IAExperiment}
	 * @param view 
	 */
	public IFuture executeExperiment(UUID session, IAExperiment experiment);
	
	/**
	 * Return the model which the service support
	 * @return Set<Modeltype>
	 */
	public Set<Modeltype> supportedModels();
}
