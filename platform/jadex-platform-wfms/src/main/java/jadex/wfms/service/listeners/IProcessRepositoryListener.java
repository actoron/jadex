package jadex.wfms.service.listeners;

import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;

/**
 * Listener which is triggered on addition and removal of process model in the repository.
 *
 */
public interface IProcessRepositoryListener extends IRemotable
{
	/**
	 * Triggered on addition of process models.
	 * @param event the event
	 */
	public IFuture processModelAdded(ProcessRepositoryEvent event);
	
	/**
	 * Triggered on removal of process models.
	 * @param event the event
	 */
	public IFuture processModelRemoved(ProcessRepositoryEvent event);
}
