package wfms.service;

import java.util.Set;

import wfms.client.IClient;
import wfms.client.IWorkitem;
import wfms.client.IWorkitemListener;

public interface IWfmsClientAccess
{
	/**
	 * Commits an acquired workitem.
	 * 
	 * @param client the client
	 * @param workitem the workitem being committed
	 */
	public void commitWorkitem(IClient client, IWorkitem workitem);
	
	/**
	 * Acquires a workitem for a client.
	 * 
	 * @param client the client
	 * @param workitem the workitem being requested for acquisition
	 * @return true, if the acquisition was successful, false otherwise
	 */
	public boolean acquireWorkitem(IClient client, IWorkitem workitem);
	
	/**
	 * Releases an acquired workitem back to the queue.
	 * 
	 * @param client the client
	 * @param workitem the workitem being released
	 */
	public void releaseWorkitem(IClient client, IWorkitem workitem);
	
	/**
	 * Returns all workitems available to a client.
	 * 
	 * @param client the client
	 * @return a set of workitems that are available for acquisition by this client
	 */
	public Set getAvailableWorkitems(IClient client);
	
	/**
	 * Adds a listener for workitem queue changes.
	 * 
	 * @param listener a new workitem listener
	 */
	public void addWorkitemListener(IWorkitemListener listener);
	
	/**
	 * Removes a listener for workitem queue changes.
	 * 
	 * @param listener a new workitem listener
	 */
	public void removeWorkitemListener(IWorkitemListener listener);
}
