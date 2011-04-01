package jadex.wfms.service.listeners;

import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;

public interface IWorkitemListener extends IRemotable
{
	/**
	 * This method is invoked when a work item is added to the work item queue.
	 * @param event the work item addition event
	 */
	public IFuture workitemAdded(WorkitemEvent event);
	
	/**
	 * This method is invoked when a work item is removed from the work item queue.
	 * @param event the work item removal event
	 */
	public IFuture workitemRemoved(WorkitemEvent event);
}
