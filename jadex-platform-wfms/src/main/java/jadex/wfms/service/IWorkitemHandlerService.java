package jadex.wfms.service;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.service.listeners.IActivityListener;
import jadex.wfms.service.listeners.IWorkitemListener;


public interface IWorkitemHandlerService
{
	public static final String SOURCE_CATEGORY_WORKITEM = "Workitem";
	public static final String SOURCE_CATEGORY_ACTIVITY = "Activity";
	
	/**
	 * Returns the current activities for all users
	 * 
	 * @return Map of current activities for all users.
	 */
	public IFuture getUserActivities();
	
	/**
	 * Terminates the activity of a user.
	 * 
	 * @param activity the activity
	 * @return Null, when done.
	 */
	public IFuture terminateActivity(IClientActivity activity);
	
	/**
	 *  Finishes an Activity.
	 *  @param userName the user name
	 *  @param workitem the activity being finished
	 *  @return Null, when done.
	 */
	public IFuture finishActivity(String userName, IClientActivity activity);
	
	/**
	 *  Begins an activity for a client.
	 *  @param userName the user name
	 *  @param workitem the workitem being requested for the activity
	 *  @return Null, when done.
	 */
	public IFuture beginActivity(String userName, IWorkitem workitem);
	
	/**
	 *  Cancel an activity.
	 *  @param userName the user name
	 *  @param activity the activity being canceled
	 *  @return Null, when done.
	 */
	public IFuture cancelActivity(String userName, IClientActivity activity);
	
	public IFuture getAvailableWorkitems(String userName);
	
	/**
	 *  Returns all activities available to a client.
	 *  @param userName the user name
	 *  @return a set of activities that are available for this client
	 */
	public IFuture getAvailableActivities(String userName);
	
	/**
	 *  Adds a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture addWorkitemListener(IComponentIdentifier client, IWorkitemListener listener);
	
	/**
	 *  Removes a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture removeWorkitemListener(IComponentIdentifier client, IWorkitemListener listener);
	
	/**
	 *  Adds a listener for activity changes.
	 *  @param listener a new activity listener
	 *  @return Null, when done.
	 */
	public IFuture addGlobalActivityListener(IComponentIdentifier client, IActivityListener listener);
	
	/**
	 *  Removes a listener for activity changes.
	 *  @param listener activity listener
	 *  @return Null, when done.
	 */
	public IFuture removeGlobalActivityListener(IComponentIdentifier client, IActivityListener listener);
	
	/**
	 *  Adds a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture addActivityListener(IComponentIdentifier client, IActivityListener listener);
	
	/**
	 *  Removes a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture removeActivityListener(IComponentIdentifier client, IActivityListener listener);
	
	/**
	 *  Queues a new Workitem.
	 *  @param workitem the workitem
	 *  @param listener result listener
	 *  @return Null, when done.
	 */
	public IFuture queueWorkitem(IWorkitem workitem, IResultListener listener);
	
	/**
	 *  Withdraws a Workitem/Activity.
	 *  @param workitem The workitem being withdrawn.
	 *  @return Null, when done.
	 */
	public IFuture withdrawWorkitem(IWorkitem workitem);
}
