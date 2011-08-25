package jadex.simulation.analysis.application.standalone;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.basic.analysis.ABasicAnalysisSessionService;

import java.util.UUID;

public class ADatabasedService extends ABasicAnalysisSessionService
{

	public ADatabasedService(IExternalAccess access, Class serviceInterface, Boolean concurrent)
	{
		super(access, serviceInterface, concurrent);
	}

	@Override
	public IFuture createSession(IAParameterEnsemble configuration)
	{
		synchronized (mutex)
		{
			UUID id = UUID.randomUUID();
			if (configuration == null) configuration = new AParameterEnsemble("Session Konfiguration");
			sessions.put(id, configuration);
			configuration.setEditable(false);
			sessionViews.put(id, new ADataSessionView(this, id, configuration));
			serviceChanged(new AServiceEvent(this, AConstants.SERVICE_SESSION_START, id));
			return new Future(id);
		}
	}

}
