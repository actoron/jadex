package jadex.simulation.analysis.service.dataBased.parameterize;

import java.util.UUID;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.ADataObject;
import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;

public interface IADatenobjekteParametrisierenGUIService extends IAnalysisSessionService
{
	public IFuture engineerGuiDataObject(UUID sessionId, IADataObject dataObject);

}
