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
	//TODO: IMPLEMENT concurrent boolean
	protected Boolean concurrent = true;  
	protected Map<String, IAParameterEnsemble> sessions;
	protected Map<String, IASessionView> sessionViews;

	public ABasicAnalysisSessionService(IExternalAccess access, Class serviceInterface, Boolean concurrent)
	{
		super(access,serviceInterface);
		synchronized (mutex)
		{
			this.concurrent = concurrent;
			sessions = Collections.synchronizedMap(new HashMap<String, IAParameterEnsemble>());
			sessionViews = Collections.synchronizedMap(new HashMap<String, IASessionView>());
			Map prop = getPropertyMap();
			prop.put(IAbstractViewerPanel.PROPERTY_VIEWERCLASS, "jadex.simulation.analysis.common.superClasses.service.view.session.SessionServiceViewerPanel");
			setPropertyMap(prop);
		}
	}
	
	// ------ IAnalysisSessionService ------


	@Override
	public IFuture createSession(IAParameterEnsemble configuration)
	{
		synchronized (mutex)
		{
			
			String id = UUID.randomUUID().toString();
			if (configuration == null) configuration = new AParameterEnsemble("Session Konfiguration");
			sessions.put(id, configuration);
			configuration.setEditable(false);
			sessionViews.put(id, new ADefaultSessionView(this,id, configuration));
			notify(new AServiceEvent(this,AConstants.SERVICE_SESSION_START, id));
			return new Future(id.toString());
		}
	}

	@Override
	public void closeSession(String id)
	{
		synchronized (mutex)
		{
			sessionViews.get(id).getSessionProperties().getTextField("Status").setText(AConstants.SERVICE_SESSION_END);
		}
	}

	@Override
	public IFuture getSessionView(String id)
	{
		return new Future(sessionViews.get(id));
	}
	
	@Override
	public IFuture getSessions()
	{
		return new Future(sessions.keySet());
	}
	
	@Override
	public IFuture getSessionConfiguration(String id)
	{
		return new Future(sessions.get(id));
	}
	
	@Override
	public IFuture getWorkload()
	{
		if (sessions.size() > 0)
		{
			return new Future(new Double(100.0));
		} else
		{
			return  new Future(new Double(0.0));
		}
	}
}
