package jadex.simulation.analysis.common.events;


import java.util.HashSet;
import java.util.Set;

public class ADataObservable
{
	protected Object mutex = new Object();
	protected Set<IADataListener> listeners = new HashSet<IADataListener>();

	public ADataObservable()
	{
		super();
	}

	public void addDataListener(IADataListener listener)
	{
		synchronized (mutex)
		{
			listeners.add(listener);
		}
	}

	public void removeDataListener(IADataListener listener)
	{
		synchronized (mutex)
		{
			listeners.remove(listener);
		}
	}

	public void dataChanged(ADataEvent e)
	{
		synchronized (mutex)
		{
			for (IADataListener listener : listeners)
			{
				listener.dataEventOccur(e);
			}
		}
	}

}