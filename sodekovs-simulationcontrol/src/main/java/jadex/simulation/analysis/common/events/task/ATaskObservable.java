package jadex.simulation.analysis.common.events.task;


import jadex.simulation.analysis.process.basicTasks.IATaskView;

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
	public void taskChanged(ATaskEvent e)
	{
		synchronized (mutex)
		{
			//first the task views
			for (IATaskListener listener : listeners)
			{
				if (listener instanceof IATaskView)
				{
					listener.taskEventOccur(e);
				}
				
			}
			for (IATaskListener listener : listeners)
			{
				if (!(listener instanceof IATaskView))
				{
					listener.taskEventOccur(e);
				}
			}
		}
	}

}