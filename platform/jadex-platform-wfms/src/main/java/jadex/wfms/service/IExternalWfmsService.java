package jadex.wfms.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.future.IFuture;
import jadex.wfms.client.ClientInfo;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.ProcessResource;
import jadex.wfms.service.listeners.IActivityListener;
import jadex.wfms.service.listeners.ILogListener;
import jadex.wfms.service.listeners.IProcessListener;
import jadex.wfms.service.listeners.IProcessRepositoryListener;
import jadex.wfms.service.listeners.IWorkitemListener;

public interface IExternalWfmsService
{
	/**
	 *  Returns the name of the Workflow Management System.
	 *  @return Name of the Workflow Management System.
	 */
	public IFuture<IComponentIdentifier> getName();
	
	/**
	 * Authenticate a new client.
	 * @return When done, return exception if the client was not authenticated.
	 */
	public IFuture<Void> authenticate(ClientInfo info);
	
	/**
	 * Deauthenticate a client.
	 * @param client the client
	 */
	public IFuture<Void> deauthenticate();
	
	/**
	 * Returns the capabilities of the client
	 * @return set of capabilities
	 */
	public IFuture<Set<Integer>> getCapabilities();
	//public Set getCapabilities(ClientIdentifier client);
	
	/**
	 *  Starts a new process
	 *  @param info The process resource information.
	 */
	public IFuture<IComponentIdentifier> startProcess(ProcessResourceInfo info);
	
	/**
	 *  Gets the names of all available process models
	 *  @return the names of all available process models
	 */
	public IFuture<List<ProcessResourceInfo>> getModels();
	
	/**
	 *  Finishes an Activity.
	 *  @param activity the activity being finished
	 */
	public IFuture<Void> finishActivity(IClientActivity activity);
	
	/**
	 *  Begins an activity for a client.
	 *  @param workitem the workitem being requested for the activity
	 */
	public IFuture<Void> beginActivity(IWorkitem workitem);
	
	/**
	 *  Cancel an activity.
	 *  @param activity the activity being canceled
	 */
	public IFuture<Void> cancelActivity(IClientActivity activity);
	
	/**
	 *  Returns all workitems available to a client.
	 *  @return a set of workitems that are available for acquisition by this client
	 */
	public IFuture<Set<IWorkitem>> getAvailableWorkitems();
	
	/**
	 *  Returns all activities available to a client.
	 *  @return a set of activities that are available for this client
	 */
	public IFuture<Set<IClientActivity>> getAvailableActivities();
	
	/**
	 *  Adds a listener for workitem queue changes relevant to the client.
	 *  @param listener a new WFMS listener
	 */
	public IFuture<Void> addWorkitemListener(IWorkitemListener listener);
	
	/**
	 *  Removes a listener for workitem queue changes relevant to the client.
	 *  @param listener a new WFMS listener
	 */
	public IFuture<Void> removeWorkitemListener(IWorkitemListener listener);
	
	/**
	 *  Adds a listener for activity changes of the client.
	 *  @param listener a new activity listener
	 */
	public IFuture<Void> addActivityListener(IActivityListener listener);
	
	/**
	 *  Removes a listener for activity changes of the client.
	 *  @param listener a new activity listener
	 */
	public IFuture<Void> removeActivityListener(IActivityListener listener);
	
	/**
	 * Adds a process model resource to the repository
	 * @param resource The process resource.
	 */
	public IFuture<Void> addProcessResource(ProcessResource resource);
	
	/**
	 * Removes a process model resource from the repository
	 * @param info The process resource information.
	 */
	public IFuture<Void> removeProcessResource(final ProcessResourceInfo info);
	
	/**
	 * Gets a process model.
	 * @param info The process resource information.
	 * @return The model.
	 */
	//public IFuture getProcessModel(String name);
	
	/**
	 * Gets a process model information not listed in the model repository.
	 * @param info Process resource information
	 * @return The model info.
	 */
	public IFuture<IModelInfo> getProcessModelInfo(ProcessResourceInfo info);
	
	/**
	 * Gets the names of all available process models
	 * 
	 * @param client the client
	 * @return the names of all available process models
	 */
	//public IFuture getProcessModelNames(IComponentIdentifier client);
	
	/**
	 * Returns a potentially incomplete set of loadable model paths
	 * 
	 * @return set of model paths
	 */
	//public IFuture getLoadableModelPaths(IComponentIdentifier client);
	
	/**
	 * Adds a process repository listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture<Void> addProcessRepositoryListener(IProcessRepositoryListener listener);
	
	/**
	 * Removes a process repository listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture<Void> removeProcessRepositoryListener(IProcessRepositoryListener listener);
	
	/**
	 * Returns the current activities for all users
	 * 
	 * @param client the client
	 * @return current activities for all users
	 */
	public IFuture<Map<String, Set<IClientActivity>>> getUserActivities();
	
	/**
	 * Terminates the activity of a user.
	 * 
	 * @param client the client issuing the termination request
	 * @param activity the activity
	 */
	public IFuture<Void> terminateActivity(IClientActivity activity);
	
	/**
	 * Adds a user activities listener which will trigger for
	 * any activity event, even activities unrelated to the client.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture<Void> addActivitiesListener(IActivityListener listener);
	
	/**
	 * Removes a user activities listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture<Void> removeActivitiesListener(IActivityListener listener);
	
	/**
	 * Adds a log listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 * @param pastEvents True, if the listener wishes to receive past events.
	 */
	public IFuture<Void> addLogListener(ILogListener listener, boolean pastEvents);
	
	/**
	 * Removes a log listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture<Void> removeLogListener(ILogListener listener);
	
	/**
	 * Adds a process listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture<Void> addProcessListener(IProcessListener listener);
	
	/**
	 * Removes a process listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public IFuture<Void> removeProcessListener(IProcessListener listener);
}
