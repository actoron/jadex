package jadex.simulation.analysis.common.component.workflow.tasks.general;

import jadex.simulation.analysis.common.events.ATaskEvent;
import jadex.simulation.analysis.common.events.IATaskListener;

public interface IATaskObservable
{
	public abstract Object getMutex();

	public abstract void addTaskListener(IATaskListener listener);

	public abstract void removeTaskListener(IATaskListener listener);

	public abstract void taskEventOccur(ATaskEvent e);

}