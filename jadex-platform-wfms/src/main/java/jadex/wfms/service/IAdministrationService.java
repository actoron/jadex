package jadex.wfms.service;

import jadex.service.IService;
import jadex.wfms.client.IClient;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.listeners.IActivityListener;
import jadex.wfms.listeners.ILogListener;
import jadex.wfms.listeners.IProcessListener;

import java.util.Map;

public interface IAdministrationService
{
	/**
	 * Returns the current activities for all users
	 * 
	 * @param client the client
	 * @return current activities for all users
	 */
	public Map getUserActivities(IClient client);
	
	/**
	 * Terminates the activity of a user.
	 * 
	 * @param client the client issuing the termination request
	 * @param activity the activity
	 */
	public void terminateActivity(IClient client, IClientActivity activity);
	
	/**
	 * Adds a user activities listener which will trigger for
	 * any activity event, even activities unrelated to the client.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void addActivitiesListener(IClient client, IActivityListener listener);
	
	/**
	 * Removes a user activities listener.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void removeActivitiesListener(IClient client, IActivityListener listener);
	
	/**
	 * Adds a log listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void addLogListener(IClient client, ILogListener listener);
	
	/**
	 * Removes a log listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void removeLogListener(IClient client, ILogListener listener);
	
	/**
	 * Adds a process listener to the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void addProcessListener(IClient client, IProcessListener listener);
	
	/**
	 * Removes a process listener from the workflow management system.
	 * 
	 * @param client the client
	 * @param listener the listener
	 */
	public void removeProcessListener(IClient client, IProcessListener listener);
}
