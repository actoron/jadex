package jadex.simulation.analysis.service.highLevel;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.UUID;

public interface IAValidationProcessService extends IAnalysisSessionService
{
	public IFuture validate(UUID session);
}
