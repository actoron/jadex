package jadex.wfms.listeners;

/**
 * Listener which is triggered on addition and removal of process model in the repository.
 *
 */
public interface IProcessRepositoryListener
{
	/**
	 * Triggered on addition of process models.
	 * @param event the event
	 */
	public void processModelAdded(ProcessRepositoryEvent event);
	
	/**
	 * Triggered on removal of process models.
	 * @param event the event
	 */
	public void processModelRemoved(ProcessRepositoryEvent event);
}
