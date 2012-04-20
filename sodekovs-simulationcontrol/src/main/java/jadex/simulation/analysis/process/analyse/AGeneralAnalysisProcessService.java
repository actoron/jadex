package jadex.simulation.analysis.process.analyse;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.superClasses.service.analysis.ASubProcessService;
import jadex.simulation.analysis.service.highLevel.IAGeneralAnalysisProcessService;

import java.util.UUID;
/**
 * AGeneralAnalysisProcessService Implementation
 * @author 5Haubeck
 *
 */
public class AGeneralAnalysisProcessService extends ASubProcessService implements IAGeneralAnalysisProcessService
{
	public AGeneralAnalysisProcessService(IExternalAccess access)
	{
		super(access, IAGeneralAnalysisProcessService.class);
	}

	@Override
	public IFuture analyse(String session)
	{
		return startSubprocess(session, "AllgemeineAnalyse", "jadex/simulation/analysis/process/analyse/AllgemeineAnalyse.bpmn", null);
	}
}
