package jadex.simulation.analysis.process.analyse.ausfuehren;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.simulation.analysis.common.data.IAExperiment;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.service.basic.analysis.ASubProcessService;
import jadex.simulation.analysis.service.highLevel.IAGeneralPlanningService;
import jadex.simulation.analysis.service.highLevel.IAGeneralExecuteService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AAllgemeinAusfuehrenService extends ASubProcessService implements IAGeneralExecuteService
{
	public AAllgemeinAusfuehrenService(IExternalAccess access)
	{
		super(access, IAGeneralExecuteService.class);
	}

	@Override
	public IFuture execute(UUID session, IAExperimentBatch experiment)
	{
		synchronized (mutex)
		{
			Map arguments = new HashMap();
			arguments.put("experiments", experiment);
			return startSubprocess(session, "AllgemeinAusführen", "jadex/simulation/analysis/process/analyse/ausfuehren/AllgemeinAusfuehren.bpmn", arguments);
		}
	}
}
