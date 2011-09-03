package jadex.simulation.analysis.application.jadex;

import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
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
