package jadex.wfms.service;

import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.listeners.IActivityListener;
import jadex.wfms.listeners.IWorkitemListener;

import java.util.Map;

public interface IClientConnector
{
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
	 * Authenticated a new client.
	 * @param client the new client
	 * @return true, if the client has been successfully authenticated.
	 */
	public IFuture authenticate(final IClient client);
	
	/**
	 * Deauthenticate a client.
	 * @param client the client
	 */
	public IFuture deauthenticate(final IClient client);
	
	/**
	 *  Finishes an Activity.
	 *  @param client the client
	 *  @param workitem the activity being finished
	 */
	public void finishActivity(IClient client, IClientActivity activity);
	
	/**
	 *  Begins an activity for a client.
	 *  @param client the client
	 *  @param workitem the workitem being requested for the activity
	 */
	public void beginActivity(IClient client, IWorkitem workitem);
	
	/**
	 *  Cancel an activity.
	 *  @param client the client
	 *  @param activity the activity being canceled
	 */
	public void cancelActivity(IClient client, IClientActivity activity);
	
	public IFuture getAvailableWorkitems(final IClient client);
	
	/**
	 *  Returns all activities available to a client.
	 *  @param client the client
	 *  @return a set of activities that are available for this client
	 */
	public IFuture getAvailableActivities(final IClient client);
	
	/**
	 *  Adds a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture addWorkitemListener(final IClient client, final IWorkitemListener listener);
	
	/**
	 *  Removes a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture removeWorkitemListener(final IClient client, final IWorkitemListener listener);
	
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
	
	/**
	 *  Adds a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture addActivityListener(final IClient client, final IActivityListener listener);
	
	/**
	 *  Removes a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture removeActivityListener(final IClient client, final IActivityListener listener);
}
