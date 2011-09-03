package jadex.simulation.analysis.service.continuative.validation;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.IAModelHypothesis;

public interface IAValidationService
{
	public IFuture defineExperimentsForHypothese(IAExperiment experiment,
			 IAModelHypothesis hypothesis, Double sampleSize);

	public IFuture evaluateHypothese(IAExperimentBatch experiments,
			 IAModelHypothesis hypothesis);
}
