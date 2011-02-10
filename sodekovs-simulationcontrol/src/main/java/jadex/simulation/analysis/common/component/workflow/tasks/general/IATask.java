package jadex.simulation.analysis.common.component.workflow.tasks.general;

import java.util.UUID;

import jadex.bpmn.runtime.ITask;
import jadex.simulation.analysis.common.events.ATaskEvent;
import jadex.simulation.analysis.common.events.IATaskListener;

public interface IATask extends ITask
{
	public Object getMutex();
	
	public UUID getID();

	/**
	 * Adds a Listener, who observe the state of the task
	 * @param listener the {@link IATaskListener} to add
	 */
	public void addTaskListener(IATaskListener listener);
	
	/**
	 * Removes a Listener, who observe the state of the task
	 * @param listener the {@link IATaskListener} to remove
	 */
	public void removeTaskListener(IATaskListener listener);
	
	/**
	 * Indicates a event in the task
	 * @param event of the change
	 */
	public void taskEventOccur(ATaskEvent e);
}
