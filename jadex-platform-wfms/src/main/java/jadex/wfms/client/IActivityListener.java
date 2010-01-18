package jadex.wfms.client;

public interface IActivityListener
{
	/**
	 * This method is invoked when an activity is added for the client.
	 * @param event the work item addition event
	 */
	public void activityAdded(ActivityEvent event);
	
	/**
	 * This method is invoked when an activity is removed.
	 * @param event the work item removal event
	 */
	public void activityRemoved(ActivityEvent event);
}
