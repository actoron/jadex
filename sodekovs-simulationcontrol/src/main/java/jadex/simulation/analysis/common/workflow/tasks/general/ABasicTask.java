package jadex.simulation.analysis.common.workflow.tasks.general;

import jadex.bpmn.runtime.BpmnInterpreter;
import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.future.IFuture;
import jadex.simulation.analysis.common.events.ATaskEvent;
import jadex.simulation.analysis.common.events.ATaskListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class ABasicTask implements IATask
{
	protected UUID id = UUID.randomUUID();
	protected Object mutex = new Object();
	protected Set<ATaskListener> listeners = new HashSet<ATaskListener>();
	
	@Override
	public Object getMutex()
	{
		return mutex;
	}
	
	@Override
	public UUID getID()
	{
		return id;
	}
	
	@Override
	public void addTaskListener(ATaskListener listener)
	{
		synchronized (mutex)
		{
			listeners.add(listener);
		}
	}

	@Override
	public void removeTaskListener(ATaskListener listener)
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
			for (ATaskListener listener : listeners)
			{
				listener.taskEventOccur(e);
			}
		}
	}
	
	public abstract IFuture execute(ITaskContext context, BpmnInterpreter instance);
	
}
