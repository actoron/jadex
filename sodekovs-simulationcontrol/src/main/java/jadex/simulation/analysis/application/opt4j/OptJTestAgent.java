package jadex.simulation.analysis.application.opt4j;

import jadex.bridge.service.annotation.GuiClass;
import jadex.bridge.service.search.SServiceProvider;
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
import jadex.simulation.analysis.common.data.optimisation.IAObjectiveFunction;
import jadex.simulation.analysis.common.data.parameter.ABasicParameter;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.data.simulation.Modeltype;
import jadex.simulation.analysis.common.util.controlComponentJadexPanel.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

import java.util.Random;
import java.util.UUID;

@Description("Agent offer IAOptimisationService")
 @ProvidedServices({@ProvidedService(type=IAOptimisationService.class,
 implementation=@Implementation(expression="new Opt4JOptimisationService($component.getExternalAccess())"))})
//@GuiClass(ComponentServiceViewerPanel.class)
//@Properties(
//{
//	@NameValue(name="viewerpanel.componentviewerclass", value="\"jadex.simulation.analysis.common.util.controlComponentJadexPanel.ControlComponentViewerPanel\"")
//})
public class OptJTestAgent extends MicroAgent
{
	@Override
	public IFuture<Void> executeBody()
	{
		IAOptimisationService service = (IAOptimisationService) SServiceProvider.getService(getServiceProvider(), IAOptimisationService.class).get(new ThreadSuspendable(this));
		AParameterEnsemble ensConf = new AParameterEnsemble("config");
		
		AParameterEnsemble ensSol = new AParameterEnsemble("solution");
		ensSol.addParameter(new ABasicParameter("diffusion-rate", Double.class, 50.0));
		ensSol.addParameter(new ABasicParameter("evaporation-rate", Double.class, 10.0));
		
		AParameterEnsemble ensRes = new AParameterEnsemble("result");
		ensRes.addParameter(new ABasicParameter("ticks", Double.class, Double.NaN));
		
		IAObjectiveFunction zf = new IAObjectiveFunction()
		{
			
			@Override
			public IFuture evaluate(IAParameterEnsemble ensemble)
			{
				System.out.println("ERROR");
				Double result = 10.0;
				return new Future(result);
			}

			@Override
			public Boolean MinGoal()
			{
				return Boolean.TRUE;
			}
		};
		
		IFuture<String> fut = service.configurateOptimisation(null, "Evolutionaerer Algorithmus", null, ensSol, zf, ensConf);
		String session = (String) fut.get(new ThreadSuspendable(this));
		
		IAParameterEnsemble expParameters = new AParameterEnsemble("Experiment Parameter");
		expParameters.addParameter(new ABasicParameter("Wiederholungen", Integer.class, 5));
		expParameters.addParameter(new ABasicParameter("Visualisierung", Boolean.class, Boolean.FALSE));
//		expParameters.addParameter(new ABasicParameter("Mittelwert Prozent", Double.class, 10.0));
//		expParameters.addParameter(new ABasicParameter("alpha", Double.class, 95.0));
		
		AExperiment exp = new AExperiment("base", AModelFactory.createTestAModel(Modeltype.NetLogo), expParameters, ensSol, ensRes);
		AExperimentBatch batch = new AExperimentBatch("batch1");
		batch.addExperiment(exp);
		batch = (AExperimentBatch) service.nextSolutions(session, batch).get(new ThreadSuspendable(this));
		while (!(Boolean)service.checkEndofOptimisation(session).get(new ThreadSuspendable(this)))
		{
			for (IAExperiment experiment : batch.getExperiments().values())
			{
				IAExecuteExperimentsService serviceExe = (IAExecuteExperimentsService) SServiceProvider.getService(getServiceProvider(), IAExecuteExperimentsService.class).get(new ThreadSuspendable(this));
				serviceExe.executeExperiment(null, experiment).get(new ThreadSuspendable(this));		
			}
			batch = (AExperimentBatch) service.nextSolutions(session, batch).get(new ThreadSuspendable(this));
//			System.out.println(batch);
		}
		return IFuture.DONE;
		
	}

}
