package jadex.simulation.analysis.process.analyse;

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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AAllgemeineAnalyseService extends ASubProcessService implements IAGeneralAnalysisProcessService
{
	public AAllgemeineAnalyseService(IExternalAccess access)
	{
		super(access, IAGeneralAnalysisProcessService.class);
	}
	
	@Override
	public IFuture getWorkload()
	{
		if (sessions.size() > 0)
		{
			return new Future(new Double(100.0));
		} else
		{
			return  new Future(new Double(0.0));
		}
		
	}

	@Override
	public IFuture analyse(UUID session)
	{
		return startSubprocess(session, "AllgemeineAnalyse", "jadex/simulation/analysis/process/analyse/AllgemeineAnalyse.bpmn", null);
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
