package jadex.simulation.analysis.common.component.workflow.tasks.general;

import jadex.simulation.analysis.common.events.ATaskEvent;
import jadex.simulation.analysis.common.events.IATaskListener;

import java.util.HashSet;
import java.util.Set;

public class ATaskObservable implements IATaskObservable
{

	protected Object mutex = new Object();
	protected Set<IATaskListener> listeners = new HashSet<IATaskListener>();

	public ATaskObservable()
	{
		super();
	}

	@Override
	public Object getMutex()
	{
		return mutex;
	}

	@Override
	public void addTaskListener(IATaskListener listener)
	{
		synchronized (mutex)
		{
			listeners.add(listener);
		}
	}

	@Override
	public void removeTaskListener(IATaskListener listener)
	{
		synchronized (mutex)
		{
			listeners.remove(listener);
		}
	}

	@Override
	public void taskEventOccur(ATaskEvent e)
	{
		synchronized (mutex)
		{
			for (IATaskListener listener : listeners)
			{
				listener.taskEventOccur(e);
			}
		}
	}

}