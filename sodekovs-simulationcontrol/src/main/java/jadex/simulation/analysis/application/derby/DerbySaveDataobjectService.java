package jadex.simulation.analysis.application.derby;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.common.superClasses.service.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.dataBased.persist.IASaveDataobjectService;

import java.util.HashMap;
import java.util.Map;

public class DerbySaveDataobjectService extends ABasicAnalysisSessionService implements IASaveDataobjectService
{
	public DerbySaveDataobjectService(IExternalAccess access, Class serviceInterface, Boolean concurrent)
	{
		super(access, serviceInterface, concurrent);
	}

	//TODO: USE DERBY HERE
	Map<String, IADataObject> values = new HashMap<String, IADataObject>();

	public IFuture getObjects()
	{
		return new Future(values.keySet());
	}


	@Override
	public void saveObject(IADataObject object)
	{
		synchronized (mutex)
		{
			values.put(object.getName(), object);
		}
	}

	@Override
	public IFuture loadObject(String name)
	{
		synchronized (mutex)
		{
			return new Future(values.get(name));
		}
	}
}
