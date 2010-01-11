package jadex.wfms.service;

import jadex.commons.concurrent.IResultListener;
import jadex.wfms.client.IWorkitem;

/**
 * Service for accessing the workitem queue and client event handling.
 */
public interface IWfmsClientService
{
	/**
	 * Queues a new workitem.
	 * @param workitem new workitem
	 * @param listener listener used when the workitem has been performed
	 */
	public void queueWorkitem(IWorkitem workitem, IResultListener listener);
}
