package jadex.simulation.analysis.service.continuative.optimisation;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.optimisation.IAObjectiveFunction;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.UUID;

/**
 * Service to optimize variables
 * 
 * @author 5Haubeck
 */
public interface IAOptimisationService extends IAnalysisSessionService
{
	/**
	 * Configurate optimization with IAParameterEnsemble.
	 * 
	 * @param session
	 *            session to use
	 * @param method
	 *            method to use
	 * @param methodParameter
	 *            method parameter to use
	 * @param solution
	 *            variables to use in optimization
	 * @param objective
	 *            objective function of optimization
	 * @param config
	 *            special configurations for this optimizationsession
	 * @return UUID session of configuration
	 */
	public IFuture configurateOptimisation(String session, String method, IAParameterEnsemble methodParameter, IAParameterEnsemble solution, IAObjectiveFunction objective, IAParameterEnsemble config);

	/**
	 * Returns supported methods
	 * 
	 * @return Set<String> sets of methods
	 */
	public IFuture supportedMethods();

	/**
	 * Returns Parameter of given method
	 * 
	 * @return IAParameterEnsemble method parameters
	 */
	public IFuture getMethodParameter(String methodName);

	/**
	 * Execute next step of optimization
	 * 
	 * @param session
	 *            session to use
	 * @param previousSolutions
	 *            evaluated experiments
	 * @return IAExperimentBatch next experiments to evaluate
	 */
	public IFuture nextSolutions(String session,
			IAExperimentBatch previousSolutions);

	/**
	 * Checks for termination
	 * 
	 * @param session
	 *            session of optimization
	 * @return Boolean true if optimization is terminated
	 */
	public IFuture checkEndofOptimisation(String session);

	/**
	 * Optimum of optimization
	 * 
	 * @param session
	 *            session of optimization
	 * @return IAParameterEnsemble experiment with highest objectives
	 */
	public IFuture getOptimum(String session);

	/**
	 * Value of optimization
	 * 
	 * @param session
	 *            session of optimization
	 * @return IAParameterEnsemble value of best experiment
	 */
	public IFuture getOptimumValue(String session);
}
