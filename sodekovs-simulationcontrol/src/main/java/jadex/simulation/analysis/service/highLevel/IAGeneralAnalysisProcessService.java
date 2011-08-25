package jadex.simulation.analysis.service.highLevel;

import java.util.UUID;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;

public interface IAGeneralAnalysisProcessService extends IAnalysisSessionService
{
	public IFuture analyse(UUID session);

}
