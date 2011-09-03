package jadex.simulation.analysis.common.superClasses.events;


import jadex.simulation.analysis.common.superClasses.tasks.IATaskView;

import java.util.HashSet;
import java.util.Set;

public class AObservable implements IAObservable
{

	protected Object mutex = new Object();
	protected Set<IAListener> listeners = new HashSet<IAListener>();

	public AObservable()
	{
		super();
	}

	@Override
	public Object getMutex()
	{
		return mutex;
	}

	@Override
	public void addListener(IAListener listener)
	{
		synchronized (mutex)
		{
			listeners.add(listener);
		}
	}

	@Override
	public void removeListener(IAListener listener)
	{
		synchronized (mutex)
		{
			listeners.remove(listener);
		}
	}

	@Override
	public void notify(IAEvent e)
	{
		synchronized (mutex)
		{
			//first the task views
			for (IAListener listener : listeners)
			{
				if (listener instanceof IATaskView)
				{
					listener.update(e);
				}
				
			}
			for (IAListener listener : listeners)
			{
				if (!(listener instanceof IATaskView))
				{
					listener.update(e);
				}
			}
		}
	}

}