package jadex.simulation.analysis.service.continuative.validation;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.IAModel;

public interface IAValidationService
{
	public IFuture defineHypotheseExperiments(IAModel model);

	public IFuture evaluateHypothese(IAExperimentBatch experiments);
}
