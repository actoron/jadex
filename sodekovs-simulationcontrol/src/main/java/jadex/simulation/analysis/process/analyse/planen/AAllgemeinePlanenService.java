package jadex.simulation.analysis.process.analyse.planen;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.common.util.AConstants;
import jadex.simulation.analysis.service.basic.analysis.ASubProcessService;
import jadex.simulation.analysis.service.basic.view.session.subprocess.ASubProcessView;
import jadex.simulation.analysis.service.highLevel.IAAllgemeinPlanenService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AAllgemeinePlanenService extends ASubProcessService implements IAAllgemeinPlanenService
{
	public AAllgemeinePlanenService(IExternalAccess access)
	{
		super(access, IAAllgemeinPlanenService.class);
	}

	@Override
	public IFuture planen(UUID session)
	{
		return startSubprocess(session, "AllgemeinPlanen", "jadex/simulation/analysis/process/analyse/planen/AllgemeinPlanen.bpmn", null);
	}
}
