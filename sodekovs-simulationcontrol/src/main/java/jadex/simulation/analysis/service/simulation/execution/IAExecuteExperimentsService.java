package jadex.simulation.analysis.service.simulation.execution;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.simulation.Modeltype;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.Set;
import java.util.UUID;

/**
 * Service for executing experiments.
 * 
 * @author 5Haubeck
 */
public interface IAExecuteExperimentsService extends IAnalysisSessionService
{

	/**
	 * Execute a IAExperiment
	 * 
	 * @param session
	 *            if any, already opened session
	 * @param experiment
	 *            experiment to simulate
	 * @param view
	 */
	public IFuture executeExperiment(String session, IAExperiment experiment);

	/**
	 * Return the model which the service support
	 * 
	 * @return Set<Modeltype>, supported models
	 */
	public Set<Modeltype> supportedModels();
}
