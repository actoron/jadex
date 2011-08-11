package jadex.simulation.analysis.service.dataBased.persist;

import java.util.UUID;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.ADataObject;
import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.common.data.IAModel;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisService;
import jadex.simulation.analysis.service.basic.analysis.IAnalysisSessionService;

public interface IADatenobjekteSpeichernService extends IAnalysisService
{
	
	public IFuture saveObject(IADataObject object);
	
	public IFuture loadObject(String name);

}
