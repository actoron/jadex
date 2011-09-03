package jadex.simulation.analysis.service.dataBased.visualisation;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisSessionService;

import java.util.UUID;

public interface IAVisualiseDataobjectService extends IAnalysisSessionService
{
	public IFuture show(UUID sessionId, IADataObject dataObject);
}
