package jadex.simulation.analysis.common.services;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.JPanel;

public class ABasicAnalysisSessionService extends ABasicAnalysisService implements IAnalysisSessionService
{
	protected Map<UUID, IAParameterEnsemble> sessions;
	protected Map<UUID, JComponent> sessionViews;

	public ABasicAnalysisSessionService(IExternalAccess access, Class serviceInterface)
	{
		super(access,serviceInterface);
		synchronized (mutex)
		{
			sessions = Collections.synchronizedMap(new HashMap<UUID, IAParameterEnsemble>());
			sessionViews = Collections.synchronizedMap(new HashMap<UUID, JComponent>());
		}
	}
	
	// ------ IAnalysisSessionService ------


	@Override
	public IFuture createSession(IAParameterEnsemble configuration)
	{
		synchronized (mutex)
		{
			UUID id = UUID.randomUUID();
			sessions.put(id, configuration);
			sessionViews.put(id, new JPanel());
			return new Future(id);
		}
	}

	@Override
	public void closeSession(UUID id)
	{
		synchronized (mutex)
		{
			sessions.remove(id);
			sessionViews.remove(id);
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
		return new Future(sessions);
	}
}
