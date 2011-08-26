package jadex.simulation.analysis.application.commonsMath;

import java.util.Random;
import java.util.UUID;

import org.apache.commons.math.stat.correlation.PearsonsCorrelation;

import jadex.base.gui.componentviewer.DefaultComponentServiceViewerPanel;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.annotation.GuiClass;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.common.data.AExperiment;
import jadex.simulation.analysis.common.data.AExperimentBatch;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.factories.AModelFactory;
import jadex.simulation.analysis.common.data.parameter.ABasicParameter;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.defaultViews.controlComponent.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.continuative.computation.IAConfidenceService;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;
import jadex.simulation.analysis.service.continuative.optimisation.IAObjectiveFunction;
import jadex.simulation.analysis.service.dataBased.engineering.IAEngineerDataobjectService;

/**
 * Agent offering common math services
 */
@Description(" Agent offering common math services")
public class TestOptCommonsMathAgent extends MicroAgent
{
	@Override
	public void executeBody()
	{
		IAOptimisationService service = (IAOptimisationService) SServiceProvider.getService(getServiceProvider(), IAOptimisationService.class).get(new ThreadSuspendable(this));
		AParameterEnsemble ensConf = new AParameterEnsemble("config");

		AParameterEnsemble ensSol = new AParameterEnsemble("solution");
		ensSol.addParameter(new ABasicParameter("in1", Double.class, 0.5));
		ensSol.addParameter(new ABasicParameter("in2", Double.class, 0.5));

		AParameterEnsemble ensRes = new AParameterEnsemble("result");
		ensRes.addParameter(new ABasicParameter("out1", Double.class, Double.NaN));
		ensRes.addParameter(new ABasicParameter("out2", Double.class, Double.NaN));

		IAObjectiveFunction zf = new IAObjectiveFunction()
		{

			@Override
			public IFuture evaluate(IAParameterEnsemble ensemble)
			{
				Double result = (Double) ensemble.getParameter("out1").getValue() + (Double) ensemble.getParameter("out2").getValue();
				return new Future(result);
			}

			@Override
			public Boolean MinGoal()
			{
				return Boolean.TRUE;
			}
		};
		UUID session = (UUID) service.configurateOptimisation(null, "Simplex Algorithmus", null, ensSol, zf, ensConf).get(new ThreadSuspendable(this));

		IAParameterEnsemble expParameters = new AParameterEnsemble("Experiment Parameter");
		expParameters.addParameter(new ABasicParameter("Wiederholungen", Integer.class, 10));
		expParameters.addParameter(new ABasicParameter("Visualisierung", Boolean.class, Boolean.TRUE));
		expParameters.addParameter(new ABasicParameter("Mittelwert Prozent", Double.class, 10.0));
		expParameters.addParameter(new ABasicParameter("alpha", Double.class, 95.0));

		AExperiment exp = new AExperiment("exp1", AModelFactory.createTestAModel(), expParameters, ensSol, ensRes);
		AExperimentBatch batch = new AExperimentBatch("batch1");
		batch.addExperiment(exp);
		batch = (AExperimentBatch) service.nextSolutions(session, batch).get(new ThreadSuspendable(this));

		while (!(Boolean) service.checkEndofOptimisation(session).get(new ThreadSuspendable(this)))
		{
			for (IAExperiment experiment : batch.getExperiments().values())
			{
				if (!experiment.isEvaluated())
				{
					experiment.getOutputParameter("out1").setValue(experiment.getInputParameter("in1").getValue());
					experiment.getOutputParameter("out2").setValue(experiment.getInputParameter("in2").getValue());
					experiment.setEvaluated(true);
				}
			}
			batch = (AExperimentBatch) service.nextSolutions(session, batch).get(new ThreadSuspendable(this));
		}
	}
}
