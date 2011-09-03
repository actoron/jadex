package jadex.simulation.analysis.common.superClasses.events;


/**
 * A Class with this Interface can be observed
 * @author 5Haubeck
 *
 */
public interface IAObservable
{
	/**
	 * Adds a Listener, who observe the state of the {@link IAObservable}
	 * @param listener the {@link IAListener} to add
	 */
	public abstract void addListener(IAListener listener);

	/**
	 * Removes a Listener, who observe the state of the {@link IAObservable}
	 * @param listener the {@link IAListener} to add
	 */
	public abstract void removeListener(IAListener listener);

	/**
	 * Indicates a event of the {@link IAObservable}
	 * @param event of the service that occur
	 */
	public abstract void notify(IAEvent event);
	
	/**
	 * Returns Object for concurrent access
	 * @return mutex Object
	 */
	public abstract Object getMutex();

}