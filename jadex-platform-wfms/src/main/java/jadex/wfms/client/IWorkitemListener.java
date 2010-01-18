package jadex.wfms.client;

public interface IWorkitemListener
{
	/**
	 * This method is invoked when a work item is added to the work item queue.
	 * @param event the work item addition event
	 */
	public void workitemAdded(WorkitemEvent event);
	
	/**
	 * This method is invoked when a work item is removed from the work item queue.
	 * @param event the work item removal event
	 */
	public void workitemRemoved(WorkitemEvent event);
}
