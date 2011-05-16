package jadex.simulation.analysis.common.services;

import jadex.bridge.IExternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.bridge.service.BasicService;
import jadex.simulation.analysis.common.events.service.AServiceEvent;
import jadex.simulation.analysis.common.events.service.IAServiceListener;

import java.util.HashSet;
import java.util.Set;

public class ABasicAnalysisService extends BasicService implements IAnalysisService
{
	protected Object mutex = new Object();
	protected Set<IAServiceListener> listeners = new HashSet<IAServiceListener>();

	public ABasicAnalysisService(IExternalAccess access, Class serviceInterface)
	{
		super(access.getServiceProvider().getId(), serviceInterface, null);
	}
	
	// ------ IAnalysisService ------- (IAServiceObserver)

	@Override
	public IFuture getWorkload()
	{
		return new Future(0.0);
	}
	

	@Override
	public Set<String>  getSupportedModes()
	{
		return new HashSet<String>();
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

}
