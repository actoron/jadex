package jadex.wfms.service;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.ProcessResource;
import jadex.wfms.service.listeners.IActivityListener;
import jadex.wfms.service.listeners.IProcessListener;
import jadex.wfms.service.listeners.IProcessRepositoryListener;
import jadex.wfms.service.listeners.IWorkitemListener;

import java.net.URL;

public interface IExternalWfmsService
{
	/**
	 * Authenticate a new client.
	 * @param client the new client
	 * @return true, if the client has been successfully authenticated.
	 */
	public IFuture authenticate(IComponentIdentifier client, ClientInfo info);
	
	/**
	 * Deauthenticate a client.
	 * @param client the client
	 */
	public IFuture deauthenticate(IComponentIdentifier client);
	
	/**
	 * Returns the capabilities of the client
	 * @param client the client
	 * @return set of capabilities
	 */
	public IFuture getCapabilities(IComponentIdentifier client);
	//public Set getCapabilities(ClientIdentifier client);
	
	/**
	 *  Starts a new process
	 *  @param client the client
	 *  @param name name of the process
	 */
	public IFuture startProcess(IComponentIdentifier client, String name);
	
	/**
	 *  Gets the names of all available process models
	 *  @param client the client
	 *  @return the names of all available process models
	 */
	public IFuture getModelNames(IComponentIdentifier client);
	
	/**
	 *  Finishes an Activity.
	 *  @param client the client
	 *  @param activity the activity being finished
	 */
	public IFuture finishActivity(IComponentIdentifier client, IClientActivity activity);
	
	/**
	 *  Begins an activity for a client.
	 *  @param client the client
	 *  @param workitem the workitem being requested for the activity
	 */
	public IFuture beginActivity(IComponentIdentifier client, IWorkitem workitem);
	
	/**
	 *  Cancel an activity.
	 *  @param client the client
	 *  @param activity the activity being canceled
	 */
	public IFuture cancelActivity(IComponentIdentifier client, IClientActivity activity);
	
	/**
	 *  Returns all workitems available to a client.
	 *  @param client the client
	 *  @return a set of workitems that are available for acquisition by this client
	 */
	public IFuture getAvailableWorkitems(IComponentIdentifier client);
	
	/**
	 *  Returns all activities available to a client.
	 *  @param client the client
	 *  @return a set of activities that are available for this client
	 */
	public IFuture getAvailableActivities(IComponentIdentifier client);
	
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
	 * Adds a process model resource to the repository
	 * @param client the client
	 * @param url url to the model resource
	 */
	public IFuture addProcessResource(IComponentIdentifier client, ProcessResource resource);
	
	/**
	 * Removes a process model resource from the repository
	 * @param client the client
	 * @param url url of the model resource
	 */
	public IFuture removeProcessResource(IComponentIdentifier client, String resourceName);
	
	/**
	 * Gets a process model.
	 * @param name name of the model
	 * @return the model
	 */
	public IFuture getProcessModel(IComponentIdentifier client, String name);
	
	/**
	 * Loads a process model not listed in the model repository.
	 * @param client the client
	 * @param path path of the model
	 * @param imports the imports
	 * @return the model
	 */
	public IFuture loadProcessModel(IComponentIdentifier client, String path, String[] imports);
	
	/**
	 * Gets the names of all available process models
	 * 
	 * @param client the client
	 * @return the names of all available process models
	 */
	public IFuture getProcessModelNames(IComponentIdentifier client);
	
	/**
	 * Returns a potentially incomplete set of loadable model paths
	 * 
	 * @return set of model paths
	 */
	public IFuture getLoadableModelPaths(IComponentIdentifier client);
	
	/**
	 * Adds a process repository listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture addProcessRepositoryListener(IComponentIdentifier client, IProcessRepositoryListener listener);
	
	/**
	 * Removes a process repository listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture removeProcessRepositoryListener(IComponentIdentifier client, IProcessRepositoryListener listener);
	
	/**
	 * Returns the current activities for all users
	 * 
	 * @param client the client
	 * @return current activities for all users
	 */
	public IFuture getUserActivities(IComponentIdentifier client);
	
	/**
	 * Terminates the activity of a user.
	 * 
	 * @param client the client issuing the termination request
	 * @param activity the activity
	 */
	public IFuture terminateActivity(IComponentIdentifier client, IClientActivity activity);
	
	/**
	 * Adds a user activities listener which will trigger for
	 * any activity event, even activities unrelated to the client.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture addActivitiesListener(IComponentIdentifier client, IActivityListener listener);
	
	/**
	 * Removes a user activities listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture removeActivitiesListener(IComponentIdentifier client, IActivityListener listener);
	
	/**
	 * Adds a log listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	//public IFuture addLogListener(IComponentIdentifier client, ILogListener listener);
	
	/**
	 * Removes a log listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	//public IFuture removeLogListener(IComponentIdentifier client, ILogListener listener);
	
	/**
	 * Adds a process listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture addProcessListener(IComponentIdentifier client, IProcessListener listener);
	
	/**
	 * Removes a process listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture removeProcessListener(IComponentIdentifier client, IProcessListener listener);
}
