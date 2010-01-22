package jadex.wfms.service;

import java.util.Map;

import jadex.commons.concurrent.IResultListener;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.listeners.IActivityListener;

/**
 * Service for accessing the workitem queue and client event handling.
 */
public interface IWfmsClientService
{
	/**
	 * Queues a new workitem.
	 * @param workitem new workitem
	 * @param listener listener used when the workitem has been performed
	 */
	public void queueWorkitem(IWorkitem workitem, IResultListener listener);
	
	/**
	 * Returns the current activities for all users
	 * 
	 * @return current activities for all users
	 */
	public Map getUserActivities();
	
	/**
	 * Terminates the activity of a user.
	 * 
	 * @param activity the activity
	 */
	public void terminateActivity(IClientActivity activity);
	
	/**
	 *  Adds a listener for activity changes.
	 *  @param listener a new activity listener
	 */
	public void addActivityListener(IActivityListener listener);
	
	/**
	 *  Removes a listener for activity changes.
	 *  @param listener activity listener
	 */
	public void removeActivityListener(IActivityListener listener);
}
