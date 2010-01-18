package jadex.wfms.service;

import jadex.service.IService;
import jadex.wfms.client.IClient;
import jadex.wfms.client.ILogListener;
import jadex.wfms.client.IProcessListener;

import java.util.Map;

public interface IAdministrationService extends IService
{
	/**
	 * Returns the current activities for all users
	 * 
	 * @param client the client
	 * @return current activities for all users
	 */
	public Map getUserActivities(IClient client);
	
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
