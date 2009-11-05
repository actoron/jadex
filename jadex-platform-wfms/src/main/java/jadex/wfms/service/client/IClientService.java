package jadex.wfms.service.client;

import jadex.service.IService;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.IWorkitemListener;
import jadex.wfms.service.definition.IProcessRepositoryService;

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
	public IProcessRepositoryService getProcessRepositoryService(IClient client);
	
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
//	public Set getModelNames(IClient client);
	
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
	 *  @return the corresponding activity, if the acquisition was successful, null otherwise
	 */
	public IClientActivity beginActivity(IClient client, IWorkitem workitem);
	
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
	 *  Adds a listener for workitem queue changes.
	 *  @param listener a new workitem listener
	 */
	public void addWorkitemListener(IWorkitemListener listener);
	
	/**
	 *  Removes a listener for workitem queue changes.
	 *  @param listener a new workitem listener
	 */
	public void removeWorkitemListener(IWorkitemListener listener);
}
