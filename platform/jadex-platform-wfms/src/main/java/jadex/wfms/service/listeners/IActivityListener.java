package jadex.wfms.service.listeners;

import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;

public interface IActivityListener extends IRemotable
{
	/**
	 * This method is invoked when an activity is added for the client.
	 * @param event the work item addition event
	 */
	public IFuture activityAdded(ActivityEvent event);
	
	/**
	 * This method is invoked when an activity is removed.
	 * @param event the work item removal event
	 */
	public IFuture activityRemoved(ActivityEvent event);
}
