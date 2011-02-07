package jadex.wfms.service;

import jadex.commons.future.IFuture;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.listeners.IActivityListener;
import jadex.wfms.listeners.IWorkitemListener;

/**
 *  Interface for wfms clients. 
 */
public interface IClientService
{
	/**
	 * Requests the Process Definition Service
	 * 
	 * @param client the client
	 * @return the process definition service
	 */
	public IFuture getProcessDefinitionService(IClient client);
	//public IProcessDefinitionService getProcessDefinitionService(IClient client);
	
	/**
	 * Requests the Monitoring Service
	 * 
	 * @param client the client
	 * @return the process definition service
	 */
	public IFuture getMonitoringService(IClient client);
	//public IAdministrationService getMonitoringService(IClient client);
	
	/**
	 * Authenticate a new client.
	 * @param client the new client
	 * @return true, if the client has been successfully authenticated.
	 */
	public IFuture authenticate(IClient client);
	//public boolean authenticate(IClient client);
	
	/**
	 * Deauthenticate a client.
	 * @param client the client
	 */
	public IFuture deauthenticate(IClient client);
	
	/**
	 * Returns the capabilities of the client
	 * @param client the client
	 * @return set of capabilities
	 */
	public IFuture getCapabilities(IClient client);
	//public Set getCapabilities(IClient client);
	
	/**
	 *  Starts a new process
	 *  @param client the client
	 *  @param name name of the process
	 */
	public IFuture startProcess(IClient client, String name);
	
	/**
	 *  Gets the names of all available process models
	 *  @param client the client
	 *  @return the names of all available process models
	 */
	public IFuture getModelNames(IClient client);
	//public Set getModelNames(IClient client);
	
	/**
	 *  Finishes an Activity.
	 *  @param client the client
	 *  @param activity the activity being finished
	 */
	public IFuture finishActivity(IClient client, IClientActivity activity);
	
	/**
	 *  Begins an activity for a client.
	 *  @param client the client
	 *  @param workitem the workitem being requested for the activity
	 */
	public IFuture beginActivity(IClient client, IWorkitem workitem);
	
	/**
	 *  Cancel an activity.
	 *  @param client the client
	 *  @param activity the activity being canceled
	 */
	public IFuture cancelActivity(IClient client, IClientActivity activity);
	
	/**
	 *  Returns all workitems available to a client.
	 *  @param client the client
	 *  @return a set of workitems that are available for acquisition by this client
	 */
	public IFuture getAvailableWorkitems(IClient client);
	//public Set getAvailableWorkitems(IClient client);
	
	/**
	 *  Returns all activities available to a client.
	 *  @param client the client
	 *  @return a set of activities that are available for this client
	 */
	public IFuture getAvailableActivities(IClient client);
	//public Set getAvailableActivities(IClient client);
	
	/**
	 *  Adds a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture addWorkitemListener(IClient client, IWorkitemListener listener);
	
	/**
	 *  Removes a listener for workitem queue changes relevant to the client.
	 *  @param client the client
	 *  @param listener a new WFMS listener
	 */
	public IFuture removeWorkitemListener(IClient client, IWorkitemListener listener);
	
	/**
	 *  Adds a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture addActivityListener(IClient client, IActivityListener listener);
	
	/**
	 *  Removes a listener for activity changes of the client.
	 *  @param client the client
	 *  @param listener a new activity listener
	 */
	public IFuture removeActivityListener(IClient client, IActivityListener listener);
}
