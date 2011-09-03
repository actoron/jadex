package jadex.simulation.analysis.service.dataBased.persist;

import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.common.superClasses.service.analysis.IAnalysisService;

public interface IASaveDataobjectService extends IAnalysisService
{
	public IFuture saveObject(IADataObject object);
	
	public IFuture loadObject(String name);
}
