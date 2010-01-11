package jadex.wfms.service;

import jadex.service.IService;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.IWorkitemListener;

import java.util.Set;

/**
 *  Interface for wfms clients. 
 */
public interface IClientService extends IService
{
	/**
	 * Requests the Process Definition Service
	 * 
	 * @param client the client
	 * @return the process definition service
	 */
	public IProcessDefinitionService getProcessDefinitionService(IClient client);
	
	/**
	 * Requests the Monitoring Service
	 * 
	 * @param client the client
	 * @return the process definition service
	 */
	public IMonitoringService getMonitoringService(IClient client);
	
	/**
	 * Authenticate a new client.
	 * @param client the new client
	 * @return true, if the client has been successfully authenticated.
	 */
	public boolean authenticate(IClient client);
	
	/**
	 * Deauthenticate a client.
	 * @param client the client
	 */
	public void deauthenticate(IClient client);
	
	/**
	 *  Starts a new process
	 *  @param client the client
	 *  @param name name of the process
	 */
	public void startProcess(IClient client, String name);
	
	/**
	 *  Gets the names of all available process models
	 *  @param client the client
	 *  @return the names of all available process models
	 */
	public Set getModelNames(IClient client);
	
	/**
	 *  Finishes an Activity.
	 *  @param client the client
	 *  @param activity the activity being finished
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
	
	/**
	 *  Returns all workitems available to a client.
	 *  @param client the client
	 *  @return a set of workitems that are available for acquisition by this client
	 */
	public Set getAvailableWorkitems(IClient client);
	
	/**
	 *  Returns all activities available to a client.
	 *  @param client the client
	 *  @return a set of activities that are available for this client
	 */
	public Set getAvailableActivities(IClient client);
	
	/**
	 *  Adds a listener for workitem queue changes and other WFMS changes.
	 *  @param listener a new WFMS listener
	 */
	public void addWfmsListener(IWorkitemListener listener);
	
	/**
	 *  Removes a listener for workitem queue changes and other WFMS changes.
	 *  @param listener a new WFMS listener
	 */
	public void removeWfmsListener(IWorkitemListener listener);
}
