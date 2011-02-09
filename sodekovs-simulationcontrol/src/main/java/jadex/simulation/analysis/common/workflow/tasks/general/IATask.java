package jadex.simulation.analysis.common.workflow.tasks.general;

import java.util.UUID;

import jadex.simulation.analysis.common.events.ATaskEvent;
import jadex.simulation.analysis.common.events.ATaskListener;

public interface IATask
{
	public Object getMutex();
	
	public UUID getID();

	/**
	 * Adds a Listener, who observe the state of the task
	 * @param listener the {@link ATaskListener} to add
	 */
	public void addTaskListener(ATaskListener listener);
	
	/**
	 * Removes a Listener, who observe the state of the task
	 * @param listener the {@link ATaskListener} to remove
	 */
	public void removeTaskListener(ATaskListener listener);
	
	/**
	 * Indicates a event in the task
	 * @param event of the change
	 */
	public void taskEventOccur(ATaskEvent e);
}
