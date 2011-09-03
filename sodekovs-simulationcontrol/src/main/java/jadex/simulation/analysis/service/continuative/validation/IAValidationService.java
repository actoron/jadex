package jadex.simulation.analysis.service.continuative.validation;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.IAModelHypothesis;

/**
 * Service to define experiments an evaluate hypothesis
 * 
 * @author 5Haubeck
 */
public interface IAValidationService
{
	/**
	 * Define some experiments to evaluate hypothesis
	 * 
	 * @param experiment
	 *            normal experiment
	 * @param hypothesis
	 *            hypothesis to evaluate
	 * @param sampleSize
	 *            number of experiments to define
	 * @return IAExperimentBatch, experiments to evaluate
	 */
	public IFuture defineExperimentsForHypothesis(IAExperiment experiment,
				IAModelHypothesis hypothesis, Double sampleSize);

	/**
	 * Evaluate the hypothesis with given (evaluated) experiments
	 * 
	 * @param experiments
	 *            evaluated experiments
	 * @param hypothesis
	 *            hypothesis to evaluate
	 */
	public IFuture evaluateHypothesis(IAExperimentBatch experiments,
				IAModelHypothesis hypothesis);
}
