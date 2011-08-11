package jadex.simulation.analysis.common.events.data;

import jadex.simulation.analysis.common.data.IADataObject;


public interface IADataObservable
{

	/**
	 * Adds a Listener, who observe the state of the object
	 * @param listener the {@link IADataListener} to add
	 */
	public abstract void addDataListener(IADataListener listener);

	/**
	 * Removes a Listener, who observe the state of the object
	 * @param listener the {@link IADataListener} to remove
	 */
	public abstract void removeDataListener(IADataListener listener);

	/**
	 * Indicates a change in data
	 * @param event of the change
	 */
	public abstract void dataChanged(ADataEvent e);
	
	/**
	 * Synchronize Object of the {@link IADataObject}
	 * @return mutex of the dataObject
	 */
	public Object getMutex();

}