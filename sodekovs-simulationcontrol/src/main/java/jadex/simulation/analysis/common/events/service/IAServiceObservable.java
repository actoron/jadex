package jadex.simulation.analysis.common.events.service;

import jadex.simulation.analysis.common.dataObjects.IADataObject;

public interface IAServiceObservable
{
	/**
	 * Adds a Listener, who observe the state of the service
	 * @param listener the {@link IAServiceListener} to add
	 */
	public abstract void addServiceListener(IAServiceListener listener);

	/**
	 * Removes a Listener, who observe the state of the service
	 * @param listener the {@link IAServiceListener} to remove
	 */
	public abstract void removeServiceListener(IAServiceListener listener);

	/**
	 * Indicates a event of the service
	 * @param event of the service that occur
	 */
	public abstract void serviceChanged(AServiceEvent e);
	
	/**
	 * Synchronize Object of the {@link IADataObject}
	 * @return mutex of the dataObject
	 */
	public Object getMutex();
}
