package jadex.wfms.service;

import jadex.wfms.client.IWorkitem;

/**
 * Service for accessing the workitem queue and client event handling.
 */
public interface IWfmsClientService
{
	/**
	 * Queues a new workitem.
	 * @param workitem new workitem
	 */
	public void queueWorkitem(IWorkitem workitem);
	
	/**
	 * Fires a process finished event.
	 * @param instanceId ID 
	 */
	public void fireProcessFinished(Object instanceId);
}
