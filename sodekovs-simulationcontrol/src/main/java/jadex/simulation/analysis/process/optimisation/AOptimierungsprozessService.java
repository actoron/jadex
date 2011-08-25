package jadex.simulation.analysis.process.optimisation;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.simulation.analysis.application.standalone.ADataSessionView;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.basic.analysis.ASubProcessService;
import jadex.simulation.analysis.service.basic.view.session.subprocess.ASubProcessView;
import jadex.simulation.analysis.service.highLevel.IAGeneralPlanningService;
import jadex.simulation.analysis.service.highLevel.IAGeneralExecuteService;
import jadex.simulation.analysis.service.highLevel.IAGeneralAnalysisProcessService;
import jadex.simulation.analysis.service.highLevel.IAOptimisationProcessService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AOptimierungsprozessService extends ASubProcessService implements IAOptimisationProcessService
{
	public AOptimierungsprozessService(IExternalAccess access)
	{
		super(access, IAOptimisationProcessService.class);
	}
	
	@Override
	public IFuture optimieren(UUID session)
	{
		return startSubprocess(session, "Optimierung", "jadex/simulation/analysis/process/optimisation/Optimierung.bpmn", null);
	}
	
	@Override
	public IFuture createSession(IAParameterEnsemble configuration)
	{
			UUID id = UUID.randomUUID();
			if (configuration == null) configuration = new AParameterEnsemble("Session Konfiguration");
			sessions.put(id, configuration);
			configuration.setEditable(false);
			sessionViews.put(id, new ASubProcessView());
			serviceChanged(new AServiceEvent(this,AConstants.SERVICE_SESSION_START, id));
			return new Future(id);
	}
}
