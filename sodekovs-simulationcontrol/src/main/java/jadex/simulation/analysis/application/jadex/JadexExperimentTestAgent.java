package jadex.simulation.analysis.application.jadex;

import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.annotation.GuiClass;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.simulation.analysis.common.defaultViews.controlComponent.ComponentServiceViewerPanel;
import jadex.simulation.analysis.service.continuative.optimisation.IAOptimisationService;
import jadex.simulation.analysis.service.simulation.execution.IAExecuteExperimentsService;

@Description("Agent offer IAExecuteExperimentsService")
public class JadexExperimentTestAgent extends MicroAgent
{
	
	@Override
	public void executeBody()
	{
		IAExecuteExperimentsService service = (IAExecuteExperimentsService) SServiceProvider.getService(getServiceProvider(), IAExecuteExperimentsService.class).get(new ThreadSuspendable(this));
		service.executeExperiment(null, null);
	}


}
