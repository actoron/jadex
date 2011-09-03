package jadex.simulation.analysis.common.superClasses.service.analysis;

import jadex.base.gui.componentviewer.IAbstractViewerPanel;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.data.parameter.AParameterEnsemble;
import jadex.simulation.analysis.common.data.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.superClasses.events.service.AServiceEvent;
import jadex.simulation.analysis.common.superClasses.service.view.session.ADefaultSessionView;
import jadex.simulation.analysis.common.superClasses.service.view.session.IASessionView;
import jadex.simulation.analysis.common.util.AConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ABasicAnalysisSessionService extends ABasicAnalysisService implements IAnalysisSessionService
{
	//TODO: IMPLEMENT concurrent
	protected Boolean concurrent = true;  
	protected Map<UUID, IAParameterEnsemble> sessions;
	protected Map<UUID, IASessionView> sessionViews;

	public ABasicAnalysisSessionService(IExternalAccess access, Class serviceInterface, Boolean concurrent)
	{
		super(access,serviceInterface);
		synchronized (mutex)
		{
			this.concurrent = concurrent;
			sessions = Collections.synchronizedMap(new HashMap<UUID, IAParameterEnsemble>());
			sessionViews = Collections.synchronizedMap(new HashMap<UUID, IASessionView>());
			Map prop = getPropertyMap();
			prop.put(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, "jadex.simulation.analysis.common.superclasses.service.view.session.SessionServiceViewerPanel");
			setPropertyMap(prop);
		}
	}
	
	// ------ IAnalysisSessionService ------


	@Override
	public IFuture createSession(IAParameterEnsemble configuration)
	{
		synchronized (mutex)
		{
			
			UUID id = UUID.randomUUID();
			if (configuration == null) configuration = new AParameterEnsemble("Session Konfiguration");
			sessions.put(id, configuration);
			configuration.setEditable(false);
			sessionViews.put(id, new ADefaultSessionView(this,id, configuration));
			notify(new AServiceEvent(this,AConstants.SERVICE_SESSION_START, id));
			return new Future(id);
		}
	}

	@Override
	public void closeSession(UUID id)
	{
		synchronized (mutex)
		{
			sessionViews.get(id).getSessionProperties().getTextField("Status").setText(AConstants.SERVICE_SESSION_END);
		}
	}

	@Override
	public IFuture getSessionView(UUID id)
	{
		return new Future(sessionViews.get(id));
	}
	
	@Override
	public IFuture getSessions()
	{
		return new Future(sessions.keySet());
	}
	
	@Override
	public IFuture getSessionConfiguration(UUID id)
	{
		return new Future(sessions.get(id));
	}
}
