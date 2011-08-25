package jadex.simulation.analysis.application.hibernate;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.application.standalone.ADatenobjekteSpeichernView;
import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.service.basic.analysis.ABasicAnalysisService;
import jadex.simulation.analysis.service.dataBased.persist.IASaveDataobjectService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HibernateSaveDataobjectService extends ABasicAnalysisService implements IASaveDataobjectService
{
	Map<String, IADataObject> saveMap;
	

	public HibernateSaveDataobjectService(IExternalAccess instance)
	{
		super(instance, IASaveDataobjectService.class);
		synchronized (mutex)
		{
			saveMap = Collections.synchronizedMap(new HashMap<String, IADataObject>());
			view = new ADatenobjekteSpeichernView(this);
		}
		
	}

	@Override
	public IFuture saveObject(IADataObject object)
	{
		saveMap.put(object.getName(), object);
		((ADatenobjekteSpeichernView)view).addObject(object.getName());
		
		return new Future(object);
	}

	@Override
	public IFuture loadObject(String name)
	{
		return new Future(saveMap.get(name));
	}
	
	public IFuture getElements()
	{
		return new Future(saveMap);
	}

}
