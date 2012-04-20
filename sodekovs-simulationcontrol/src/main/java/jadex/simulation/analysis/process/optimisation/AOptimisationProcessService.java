package jadex.simulation.analysis.process.optimisation;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.service.AServiceEvent;
import jadex.simulation.analysis.common.superClasses.service.analysis.ASubProcessService;
import jadex.simulation.analysis.common.superClasses.service.view.session.subprocess.ASubProcessView;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.highLevel.IAOptimisationProcessService;

import java.util.UUID;

public class AOptimisationProcessService extends ASubProcessService implements IAOptimisationProcessService
{
	public AOptimisationProcessService(IExternalAccess access)
	{
		super(access, IAOptimisationProcessService.class);
	}
	
	@Override
	public IFuture optimize(String session)
	{
		return startSubprocess(session, "Optimierung", "jadex/simulation/analysis/process/optimisation/Optimierung.bpmn", null);
	}
	
	@Override
	public IFuture createSession(IAParameterEnsemble configuration)
	{
		String id = UUID.randomUUID().toString();
			if (configuration == null) configuration = new AParameterEnsemble("Session Konfiguration");
			sessions.put(id, configuration);
			configuration.setEditable(false);
			sessionViews.put(id, new ASubProcessView());
			notify(new AServiceEvent(this,AConstants.SERVICE_SESSION_START, id));
			return new Future(id);
	}
}
