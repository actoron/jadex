package jadex.wfms.service.client;

import jadex.wfms.client.IWorkitem;

/**
 * Service for accessing the workitem queue.
 */
public interface IWorkitemQueueService
{
	/**
	 * Queues a new workitem.
	 * @param workitem new workitem
	 */
	public void queueWorkitem(IWorkitem workitem);
}
