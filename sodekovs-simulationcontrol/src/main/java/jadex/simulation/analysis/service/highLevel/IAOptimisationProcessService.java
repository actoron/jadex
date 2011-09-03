package jadex.simulation.analysis.service.highLevel;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.UUID;

public interface IAOptimisationProcessService extends IAnalysisSessionService
{
	public IFuture optimieren(UUID session);
}
