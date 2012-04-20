package jadex.simulation.analysis.application.standalone;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.IADataObject;
import jadex.simulation.analysis.common.superClasses.service.analysis.ABasicAnalysisSessionService;
import jadex.simulation.analysis.service.dataBased.visualisation.IAVisualiseDataobjectService;

import java.util.UUID;

public class AVisualiseDataobjectService extends ABasicAnalysisSessionService implements IAVisualiseDataobjectService
{

	public AVisualiseDataobjectService(IExternalAccess instance)
	{
		super(instance, IAVisualiseDataobjectService.class, true);
	}

	@Override
	public IFuture show(String sessionId, IADataObject dataObject)
	{
			if (sessionId == null) sessionId = (String) createSession(null).get(susThread);
			ADataSessionView view = (ADataSessionView) sessionViews.get(sessionId);
			view.startGUI(dataObject);
			 sessionViews.put(sessionId,view);
			return new Future(sessionId);
	}

}
