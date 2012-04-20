package jadex.simulation.analysis.application.commonsMath;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.AExperimentBatch;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.validation.IAModelHypothesis;
import jadex.simulation.analysis.common.superClasses.service.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.continuative.validation.IAValidationService;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class CommonsMathValidationService extends ABasicAnalysisSessionService implements IAValidationService
{
	private PearsonsCorrelation corr = new PearsonsCorrelation();

	public CommonsMathValidationService(IExternalAccess access)
	{
		super(access, IAValidationService.class, true);
	}

	@Override
	public IFuture defineExperimentsForHypothesis(IAExperiment experiment, IAModelHypothesis hypothesis, Double sampleSize)
	{
		IAExperimentBatch expBatch = new AExperimentBatch("Evaluate Experiments");
		String name = hypothesis.getInputParameter().getName();
		Double value = (Double) experiment.getConfigParameter(name).getValue();
		Double upper = 1.5 * value;
		Double lower = 1.5 * value;
		Double diff = (upper-lower)/sampleSize;
		for (int i = 0; i < sampleSize; i++)
		{
			IAExperiment cloneExp = (IAExperiment) experiment.clonen();
			cloneExp.getConfigParameter(name).setValue(lower+ diff*i);
			expBatch.addExperiment(cloneExp);
		}
		
		return new Future(expBatch);
	}

	@Override
	public IFuture evaluateHypothesis(IAExperimentBatch experiments, IAModelHypothesis hypothesis)
	{
		double[] xArray = new double[experiments.getExperiments().size()];
		double[] yArray = new double[experiments.getExperiments().size()];
		int i = 0;
		for (IAExperiment exp : experiments.getExperiments().values())
		{
			yArray[i] = (Double) exp.getExperimentParameter(hypothesis.getOutputParameter().getName()).getValue();
			xArray[i] = (Double) exp.getConfigParameter(hypothesis.getInputParameter().getName()).getValue();
		}
		return new Future(corr.correlation(xArray, yArray));
	}

}
