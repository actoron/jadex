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
import jadex.simulation.analysis.common.util.controlComponentJadexPanel.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;

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
		ensSol.addParameter(new ABasicParameter("in1", Double.class, 5.0));
		ensSol.addParameter(new ABasicParameter("in2", Double.class, 10.0));
		ensSol.addParameter(new ABasicParameter("in3", Double.class, 0.0));
		
		AParameterEnsemble ensRes = new AParameterEnsemble("result");
		ensRes.addParameter(new ABasicParameter("out1", Double.class, Double.NaN));
		ensRes.addParameter(new ABasicParameter("out2", Double.class, Double.NaN));
		ensRes.addParameter(new ABasicParameter("out3", Double.class, Double.NaN));
		
		IAObjectiveFunction zf = new IAObjectiveFunction()
		{
			
			@Override
			public IFuture evaluate(IAParameterEnsemble ensemble)
			{
				Double result = (Double)ensemble.getParameter("out1").getValue() + (Double)ensemble.getParameter("out2").getValue() + (Double)ensemble.getParameter("out3").getValue();
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
		expParameters.addParameter(new ABasicParameter("Wiederholungen", Integer.class, 10));
		expParameters.addParameter(new ABasicParameter("Visualisierung", Boolean.class, Boolean.TRUE));
		expParameters.addParameter(new ABasicParameter("Mittelwert Prozent", Double.class, 10.0));
		expParameters.addParameter(new ABasicParameter("alpha", Double.class, 95.0));
		
		AExperiment exp = new AExperiment("exp1", AModelFactory.createTestAModel(), expParameters, ensSol, ensRes);
		AExperimentBatch batch = new AExperimentBatch("batch1");
		batch.addExperiment(exp);
		batch = (AExperimentBatch) service.nextSolutions(session, batch).get(new ThreadSuspendable(this));
		while (!(Boolean)service.checkEndofOptimisation(session).get(new ThreadSuspendable(this)))
		{
			for (IAExperiment experiment : batch.getExperiments().values())
			{
				experiment.getResultParameter("out1").setValue(new Double(2));
				experiment.getResultParameter("out2").setValue(new Double(1));
				experiment.getResultParameter("out3").setValue(new Double(3));
			}
			batch = (AExperimentBatch) service.nextSolutions(session, batch).get(new ThreadSuspendable(this));
			System.out.println(batch);
		}
		return IFuture.DONE;
		
	}

}
