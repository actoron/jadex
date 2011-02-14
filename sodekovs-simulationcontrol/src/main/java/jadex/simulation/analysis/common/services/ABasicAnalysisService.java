package jadex.simulation.analysis.common.services;

import jadex.bdi.runtime.ICapability;
import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.service.BasicService;
import jadex.simulation.analysis.common.dataObjects.parameter.IAParameterEnsemble;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.common.events.service.IAServiceListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ABasicAnalysisService extends BasicService implements IAnalysisService
{
	protected Object mutex = new Object();
	protected Set<IAServiceListener> listeners = new HashSet<IAServiceListener>();
	protected Map<UUID, IAParameterEnsemble> sessions;

	public ABasicAnalysisService(IExternalAccess access)
	{
		super(access.getServiceProvider().getId(), IAnalysisService.class, null);
		synchronized (mutex)
		{
			sessions = Collections.synchronizedMap(new HashMap<UUID, IAParameterEnsemble>());
		}
	}
	
	// ------ IAnalysisService ------- (IAServiceObserver)

	@Override
	public IFuture getWorkload()
	{
		return new Future(AWorkload.IDLE);
	}
	
	@Override
	public Object getMutex()
	{
		return mutex;
	}

	@Override
	public void addServiceListener(IAServiceListener listener)
	{
		synchronized (mutex)
		{
			listeners.add(listener);
		}	
	}

	@Override
	public void removeServiceListener(IAServiceListener listener)
	{
		synchronized (mutex)
		{
			listeners.remove(listener);
		}	
	}

	@Override
	public void serviceChanged(AServiceEvent e)
	{
		synchronized (mutex)
		{
			for (IAServiceListener listener : listeners)
			{
				listener.serviceEventOccur(e);
			}
		}	
	}

	@Override
	public IFuture createSession(IAParameterEnsemble configuration)
	{
		synchronized (mutex)
		{
			UUID id = UUID.randomUUID();
			sessions.put(id, configuration);
			return new Future(id);
		}
	}

	@Override
	public void closeSession(UUID id)
	{
		synchronized (mutex)
		{
			sessions.remove(id);
		}
	}
}
