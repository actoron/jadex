package jadex.simulation.analysis.common.events.task;


public interface IATaskObservable
{
	//TODO: Comments
	public abstract void addTaskListener(IATaskListener listener);

	public abstract void removeTaskListener(IATaskListener listener);

	public abstract void taskChanged(ATaskEvent e);
	
	public abstract Object getMutex();

}