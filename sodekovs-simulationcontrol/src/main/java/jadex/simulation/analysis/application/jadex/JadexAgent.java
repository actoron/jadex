package jadex.simulation.analysis.application.jadex;

import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.annotation.GuiClass;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.factories.AExperimentFactory;
import jadex.simulation.analysis.common.data.parameter.IAParameter;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.defaultViews.controlComponent.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.highLevel.IAGeneralAnalysisProcessService;
import jadex.simulation.analysis.service.simulation.Modeltype;
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

@Description("Agent offer IAExecuteExperimentsService")
@ProvidedServices({ @ProvidedService(type = IAExecuteExperimentsService.class,
		implementation = @Implementation(expression = "new JadexExecuteExperimentsService($component.getExternalAccess())")) })
@GuiClass(ComponentServiceViewerPanel.class)
@Properties({ @NameValue(name = "viewerpanel.componentviewerclass", value = "\"jadex.simulation.analysis.common.defaultViews.controlComponent.ControlComponentViewerPanel\"") })
public class JadexAgent extends MicroAgent
{
	@Override
	public void executeBody()
	{
		IAExecuteExperimentsService service = (IAExecuteExperimentsService) SServiceProvider.getService(getServiceProvider(), IAExecuteExperimentsService.class).get(new ThreadSuspendable(this));
		IAExperiment experiment = AExperimentFactory.createTestAExperiment(Modeltype.Jadex);
		service.executeExperiment(null, experiment).addResultListener(new IResultListener()
		{
			
			@Override
			public void resultAvailable(Object result)
			{
				IAExperiment exp = (IAExperiment) result;
				for (IAParameter para : exp.getOutputParameters().getParameters().values())
				{
					System.out.println(para.getName() + "==" + para.getValue());
				}
				
			}
			
			@Override
			public void exceptionOccurred(Exception exception)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
	}

}
