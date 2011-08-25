package jadex.simulation.analysis.application.standalone;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.service.dataBased.visualisation.IAVisualiseDataobjectService;

import java.util.UUID;

public class AVisualiseDataobjectService extends ADatabasedService implements IAVisualiseDataobjectService
{

	public AVisualiseDataobjectService(IExternalAccess instance)
	{
		super(instance, IAVisualiseDataobjectService.class, true);
	}

	@Override
	public IFuture show(UUID sessionId, IADataObject dataObject)
	{
		synchronized (mutex)
		{
			if (sessionId == null) sessionId = (UUID) createSession(null).get(susThread);
			ADataSessionView view = (ADataSessionView) sessionViews.get(sessionId);
			return view.startGUI(dataObject);
		}
	}

}
