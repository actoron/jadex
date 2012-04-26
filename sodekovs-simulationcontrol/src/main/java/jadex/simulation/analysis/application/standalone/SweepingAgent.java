package jadex.simulation.analysis.application.standalone;

import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.simulation.analysis.common.GnuLogger;
import jadex.simulation.analysis.common.data.AExperiment;
import jadex.simulation.analysis.common.data.AExperimentBatch;
import jadex.simulation.analysis.common.data.factories.AModelFactory;
import jadex.simulation.analysis.common.data.optimisation.IAObjectiveFunction;
import jadex.simulation.analysis.common.data.parameter.ABasicParameter;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.data.simulation.Modeltype;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

@Description("Agent for Sweeping")
public class SweepingAgent extends MicroAgent
{
	@Override
	public IFuture<Void> executeBody()
	{	
		

		AParameterEnsemble ensConf = new AParameterEnsemble("config");
		
		AParameterEnsemble ensSol = new AParameterEnsemble("solution");
		ensSol.addParameter(new ABasicParameter("diffusion-rate", Double.class, 36.0));
		ensSol.addParameter(new ABasicParameter("evaporation-rate", Double.class, 85.0));
		ensSol.addParameter(new ABasicParameter("population", Double.class, 10.0));
		
		AParameterEnsemble ensRes = new AParameterEnsemble("result");
		ensRes.addParameter(new ABasicParameter("ticks", Double.class, Double.NaN));
		
		IAParameterEnsemble expParameters = new AParameterEnsemble("Experiment Parameter");
		expParameters.addParameter(new ABasicParameter("Wiederholungen", Integer.class, 10));
		expParameters.addParameter(new ABasicParameter("Visualisierung", Boolean.class, Boolean.FALSE));
//		expParameters.addParameter(new ABasicParameter("Mittelwert Prozent", Double.class, 10.0));
//		expParameters.addParameter(new ABasicParameter("alpha", Double.class, 95.0));
		
		AExperiment base = new AExperiment("base", AModelFactory.createTestAModel(Modeltype.NetLogo), expParameters, ensSol, ensRes);
//		AExperimentBatch batch = new AExperimentBatch("batch1");
//		batch.addExperiment(exp);
		
		IAExecuteExperimentsService eservice = SServiceProvider.getService(getServiceProvider(), IAExecuteExperimentsService.class).get(new ThreadSuspendable(this));
		
		for (int i = 20; i <= 200; i = i+1) {
			AExperiment exp = (AExperiment) base.clonen(); 
			exp.getConfigParameter("population").setValue(i);
			exp = (AExperiment) eservice.executeExperiment(null, exp).get(new ThreadSuspendable(this));
			exp.setEvaluated(true);
		}
		return IFuture.DONE;
		
	}

}
