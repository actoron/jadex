package jadex.simulation.analysis.process.analyse.ausfuehren;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IAExperimentBatch;
import jadex.simulation.analysis.common.superClasses.service.analysis.ASubProcessService;
import jadex.simulation.analysis.service.highLevel.IAGeneralExecuteService;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AGeneralExecuteService Implementation
 * @author 5Haubeck
 *
 */
public class AGeneralExecuteService extends ASubProcessService implements IAGeneralExecuteService
{
	public AGeneralExecuteService(IExternalAccess access)
	{
		super(access, IAGeneralExecuteService.class);
	}

	@Override
	public IFuture execute(String session, IAExperimentBatch experiment)
	{
		synchronized (mutex)
		{
			Map arguments = new HashMap();
			arguments.put("experiments", experiment);
			return startSubprocess(session, "AllgemeinAusführen", "jadex/simulation/analysis/process/analyse/ausfuehren/AllgemeinAusfuehren.bpmn", arguments);
		}
	}
}
